package jatx.russianrocksongbook.localsongs.internal.viewmodel

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import jatx.russianrocksongbook.domain.models.local.Song
import jatx.russianrocksongbook.domain.repository.cloud.result.STATUS_ERROR
import jatx.russianrocksongbook.domain.repository.cloud.result.STATUS_SUCCESS
import jatx.russianrocksongbook.domain.repository.local.*
import jatx.russianrocksongbook.localsongs.R
import jatx.russianrocksongbook.viewmodel.CommonViewModel
import jatx.russianrocksongbook.navigation.ScreenVariant
import jatx.russianrocksongbook.viewmodel.contracts.SongTextViewModelContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
internal open class LocalViewModel @Inject constructor(
    private val localStateHolder: LocalStateHolder,
    localViewModelDeps: LocalViewModelDeps
): CommonViewModel(
    localStateHolder.commonStateHolder,
    localViewModelDeps.commonViewModelDeps
), SongTextViewModelContract {
    private val getSongsByArtistUseCase =
        localViewModelDeps.getSongsByArtistUseCase
    private val getCountByArtistUseCase =
        localViewModelDeps.getCountByArtistUseCase
    private val getSongByArtistAndPositionUseCase =
        localViewModelDeps.getSongByArtistAndPositionUseCase
    private val updateSongUseCase =
        localViewModelDeps.updateSongUseCase
    private val deleteSongToTrashUseCase =
        localViewModelDeps.deleteSongToTrashUseCase
    private val addWarningLocalUseCase =
        localViewModelDeps.addWarningLocalUseCase
    private val addSongToCloudUseCase =
        localViewModelDeps.addSongToCloudUseCase
    private val getArtistsUseCase =
        localViewModelDeps.getArtistsUseCase

    val currentSongCount = localStateHolder.currentSongCount.asStateFlow()
    val currentSongList = localStateHolder.currentSongList.asStateFlow()
    val currentSongPosition = localStateHolder.currentSongPosition.asStateFlow()
    val currentSong = localStateHolder.currentSong.asStateFlow()

    val isEditorMode = localStateHolder.isEditorMode.asStateFlow()
    val isAutoPlayMode = localStateHolder.isAutoPlayMode.asStateFlow()
    val isUploadButtonEnabled = localStateHolder.isUploadButtonEnabled.asStateFlow()

    val scrollPosition = localStateHolder.scrollPosition.asStateFlow()
    val needScroll = localStateHolder.needScroll.asStateFlow()

    val editorText = mutableStateOf("")

    private var showSongsJob: Job? = null
    private var selectSongJob: Job? = null
    private var getArtistsJob: Job? = null
    private var uploadSongDisposable: Disposable? = null

    companion object {
        private const val key = "Local"

        @Composable
        fun getInstance(): LocalViewModel {
            if (!storage.containsKey(key)){
                storage[key] = hiltViewModel<LocalViewModel>()
            }
            return storage[key] as LocalViewModel
        }

        fun getStoredInstance() = storage[key] as? LocalViewModel
    }

    fun selectArtist(artist: String) {
        Log.e("select artist", artist)
        when (artist) {
            ARTIST_ADD_ARTIST -> {
                selectScreen(ScreenVariant.AddArtist)
            }
            ARTIST_ADD_SONG -> {
                selectScreen(ScreenVariant.AddSong)
            }
            ARTIST_CLOUD_SONGS -> {
                selectScreen(ScreenVariant.CloudSearch())
            }
            ARTIST_DONATION -> {
                selectScreen(ScreenVariant.Donation)
            }
            ARTIST_FAVORITE -> {
                selectScreen(
                    ScreenVariant.Favorite(
                        isBackFromSong = false
                    )
                )
            }
            else -> {
                selectScreen(
                    ScreenVariant.SongList(
                        artist = artist,
                        isBackFromSong = false
                    )
                )
            }
        }
    }

    fun showSongs(
        artist: String,
        passToSongWithTitle: String? = null
    ) {
        Log.e("show songs", artist)
        localStateHolder
            .commonStateHolder
            .currentArtist.value = artist
        localStateHolder.currentSongCount.value =
            getCountByArtistUseCase.execute(artist)
        showSongsJob?.let {
            if (!it.isCancelled) it.cancel()
        }
        var _passToSongWithTitle = passToSongWithTitle
        showSongsJob = viewModelScope
            .launch {
                withContext(Dispatchers.IO) {
                    getSongsByArtistUseCase
                        .execute(artist)
                        .collect {
                            withContext(Dispatchers.Main) {
                                val oldArtist = currentSongList.value.getOrNull(0)?.artist
                                    ?: "null"
                                val newArtist = it.getOrNull(0)?.artist ?: "null"
                                if (newArtist == artist || newArtist == "null" || artist == ARTIST_FAVORITE) {
                                    localStateHolder.currentSongList.value = it
                                    if (_passToSongWithTitle != null) {
                                        currentSongList
                                            .value
                                            .map { it.title }
                                            .indexOf(_passToSongWithTitle)
                                            .takeIf { it >= 0 }
                                            ?.let { position ->
                                                selectScreen(
                                                    ScreenVariant
                                                        .SongText(newArtist, position)
                                                )
                                            }
                                        _passToSongWithTitle = null
                                    } else if (oldArtist != newArtist) {
                                        Log.e("artists", "$oldArtist $newArtist")
                                        selectSong(0)
                                    }
                                }
                            }
                        }
                }
            }
    }

    fun selectSong(position: Int) {
        selectSongJob?.let {
            if (!it.isCancelled) it.cancel()
        }

        Log.e("select song", position.toString())
        updateScrollPosition(position)
        updateNeedScroll(true)
        localStateHolder.currentSongPosition.value = position

        val oldArtist = currentSong.value?.artist
        val oldTitle = currentSong.value?.title

        selectSongJob = viewModelScope
            .launch {
                withContext(Dispatchers.IO) {
                    getSongByArtistAndPositionUseCase
                        .execute(currentArtist.value, position)
                        .collect {
                            withContext(Dispatchers.Main) {
                                localStateHolder.currentSong.value = it

                                val newArtist = currentSong.value?.artist
                                val newTitle = currentSong.value?.title

                                if (newArtist != oldArtist || newTitle != oldTitle) {
                                    localStateHolder.isAutoPlayMode.value = false
                                    localStateHolder.isEditorMode.value = false
                                    currentSong.value?.let {
                                        editorText.value = it.text
                                    }
                                }
                            }
                        }
                }
            }
    }

    fun updateCurrentSong(song: Song) {
        localStateHolder.currentSong.value = song
    }

    fun nextSong() {
        if (currentSongCount.value > 0) {
            selectScreen(
                ScreenVariant
                .SongText(
                    artist = currentArtist.value,
                    position = (currentSongPosition.value + 1) % currentSongCount.value
                ))
        }
    }

    fun prevSong() {
        if (currentSongCount.value > 0) {
            if (currentSongPosition.value > 0) {
                selectScreen(
                    ScreenVariant
                    .SongText(
                        artist = currentArtist.value,
                        position = (currentSongPosition.value - 1) % currentSongCount.value
                    ))
            } else {
                selectScreen(
                    ScreenVariant
                    .SongText(
                        artist = currentArtist.value,
                        position = currentSongCount.value - 1
                    ))
            }
        }
    }

    fun saveSong(song: Song) {
        updateSongUseCase.execute(song)
    }

    fun setEditorMode(value: Boolean) {
        localStateHolder.isEditorMode.value = value
    }

    fun setAutoPlayMode(value: Boolean) {
        localStateHolder.isAutoPlayMode.value = value
    }

    fun setFavorite(value: Boolean) {
        Log.e("set favorite", value.toString())
        currentSong.value?.copy(favorite = value)?.let {
            updateCurrentSong(it)
            saveSong(it)
            if (!value && currentArtist.value == ARTIST_FAVORITE) {
                localStateHolder.currentSongCount.value = getCountByArtistUseCase.execute(
                    ARTIST_FAVORITE
                )
                if (currentSongCount.value > 0) {
                    if (currentSongPosition.value >= currentSongCount.value) {
                        selectScreen(
                            ScreenVariant
                            .SongText(
                                artist = currentArtist.value,
                                position = currentSongPosition.value - 1
                            ))
                    } else {
                        selectScreen(
                            ScreenVariant
                            .SongText(
                                artist = currentArtist.value,
                                position = currentSongPosition.value
                            ))
                    }
                } else {
                    back {}
                }
            }
            if (value) {
                showToast(R.string.toast_added_to_favorite)
            } else {
                showToast(R.string.toast_removed_from_favorite)
            }
        }
    }

    fun updateScrollPosition(position: Int) {
        with(localStateHolder.scrollPosition) {
            value = position
        }
    }

    fun updateNeedScroll(newValue: Boolean) {
        with(localStateHolder.needScroll) {
            value = newValue
        }
    }

    fun deleteCurrentToTrash() {
        currentSong.value?.let {
            deleteSongToTrashUseCase.execute(it)
            localStateHolder.currentSongCount.value = getCountByArtistUseCase
                .execute(currentArtist.value)
            if (currentSongCount.value > 0) {
                if (currentSongPosition.value >= currentSongCount.value) {
                    selectScreen(
                        ScreenVariant
                        .SongText(
                            artist = currentArtist.value,
                            position = currentSongPosition.value - 1
                        ))
                } else {
                    selectScreen(
                        ScreenVariant
                        .SongText(
                            artist = currentArtist.value,
                            position = currentSongPosition.value
                        ))
                }
            } else {
                back {}
            }
        }
        showToast(R.string.toast_deleted_to_trash)
    }

    override fun openVkMusicImpl(dontAskMore: Boolean) {
        settings.vkMusicDontAsk = dontAskMore
        currentSong.value?.let {
            callbacks.onOpenVkMusic("${it.artist} ${it.title}")
        }
    }

    override fun openYandexMusicImpl(dontAskMore: Boolean) {
        settings.yandexMusicDontAsk = dontAskMore
        currentSong.value?.let {
            callbacks.onOpenYandexMusic("${it.artist} ${it.title}")
        }
    }

    override fun openYoutubeMusicImpl(dontAskMore: Boolean) {
        settings.youtubeMusicDontAsk = dontAskMore
        currentSong.value?.let {
            callbacks.onOpenYoutubeMusic("${it.artist} ${it.title}")
        }
    }

    override fun sendWarningImpl(comment: String) {
        currentSong.value?.let {
            addWarningLocalUseCase
                .execute(it, comment)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    when (result.status) {
                        STATUS_SUCCESS -> showToast(R.string.toast_send_warning_success)
                        STATUS_ERROR -> showToast(result.message ?: "")
                    }
                }, { error ->
                    error.printStackTrace()
                    showToast(R.string.error_in_app)
                })
        }
    }

    fun speechRecognize(dontAskMore: Boolean) {
        settings.voiceHelpDontAsk = dontAskMore
        callbacks.onSpeechRecognize()
    }

    fun uploadCurrentToCloud() {
        currentSong.value?.let { song ->
            localStateHolder.isUploadButtonEnabled.value = false
            uploadSongDisposable?.let {
                if (!it.isDisposed) it.dispose()
            }
            uploadSongDisposable = addSongToCloudUseCase
                .execute(song)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    when (result.status) {
                        STATUS_SUCCESS -> showToast(R.string.toast_upload_to_cloud_success)
                        STATUS_ERROR -> showToast(result.message ?: "")
                    }
                    localStateHolder.isUploadButtonEnabled.value = true
                }, { error ->
                    error.printStackTrace()
                    showToast(R.string.error_in_app)
                    localStateHolder.isUploadButtonEnabled.value = true
                })
        }
    }

    fun reviewApp() {
        callbacks.onReviewApp()
    }

    fun showDevSite() {
        callbacks.onShowDevSite()
    }

    fun updateArtists() {
        getArtistsJob?.let {
            if (!it.isCancelled) it.cancel()
        }
        getArtistsJob = viewModelScope.launch {
            getArtistsUseCase
                .execute()
                .collect {
                    withContext(Dispatchers.Main) {
                        localStateHolder
                            .commonStateHolder
                            .artistList.value = it
                    }
                }
        }
    }
}
