package jatx.russianrocksongbook.localsongs.internal.viewmodel

import android.util.Log
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
import jatx.russianrocksongbook.viewmodel.CurrentScreenVariant
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

    private val currentSongCount = localStateHolder.currentSongCount.asStateFlow()
    val currentSongList = localStateHolder.currentSongList.asStateFlow()
    private val currentSongPosition = localStateHolder.currentSongPosition.asStateFlow()
    val currentSong = localStateHolder.currentSong.asStateFlow()

    val isEditorMode = localStateHolder.isEditorMode.asStateFlow()
    val isAutoPlayMode = localStateHolder.isAutoPlayMode.asStateFlow()
    val isUploadButtonEnabled = localStateHolder.isUploadButtonEnabled.asStateFlow()

    val scrollPosition = localStateHolder.scrollPosition.asStateFlow()
    var isLastOrientationPortrait = localStateHolder.isLastOrientationPortrait.asStateFlow()
    val wasOrientationChanged = localStateHolder.wasOrientationChanged.asStateFlow()
    val needScroll = localStateHolder.needScroll.asStateFlow()

    private var showSongsJob: Job? = null
    private var selectSongJob: Job? = null
    private var uploadSongDisposable: Disposable? = null

    fun selectArtist(
        artist: String,
        forceOnSuccess: Boolean = false,
        onSuccess: () -> Unit = {}
    ) {
        Log.e("select artist", artist)
        showSongsJob?.let {
            if (!it.isCancelled) it.cancel()
        }
        when (artist) {
            ARTIST_ADD_ARTIST -> {
                selectScreen(CurrentScreenVariant.ADD_ARTIST)
            }
            ARTIST_ADD_SONG -> {
                selectScreen(CurrentScreenVariant.ADD_SONG)
            }
            ARTIST_CLOUD_SONGS -> {
                selectScreen(CurrentScreenVariant.CLOUD_SEARCH)
            }
            ARTIST_DONATION -> {
                selectScreen(CurrentScreenVariant.DONATION)
            }
            else -> {
                showSongs(artist, forceOnSuccess, onSuccess)
            }
        }
    }

    private fun showSongs(
        artist: String,
        forceOnSuccess: Boolean,
        onSuccess: () -> Unit
    ) {
        localStateHolder
            .commonStateHolder
            .currentArtist.value = artist
        localStateHolder.currentSongCount.value =
            getCountByArtistUseCase.execute(artist)
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
                                localStateHolder.currentSongList.value = it
                                if (oldArtist != newArtist || forceOnSuccess) {
                                    onSuccess()
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
        localStateHolder.isAutoPlayMode.value = false
        localStateHolder.isEditorMode.value = false

        selectSongJob = viewModelScope
            .launch {
                withContext(Dispatchers.IO) {
                    getSongByArtistAndPositionUseCase
                        .execute(currentArtist.value, position)
                        .collect {
                            withContext(Dispatchers.Main) {
                                localStateHolder.currentSong.value = it
                            }
                        }
                }
            }
    }

    fun nextSong() {
        if (currentSongCount.value > 0) {
            selectSong((currentSongPosition.value + 1) % currentSongCount.value)
        }
    }

    fun prevSong() {
        if (currentSongCount.value > 0) {
            if (currentSongPosition.value > 0) {
                selectSong((currentSongPosition.value - 1) % currentSongCount.value)
            } else {
                selectSong(currentSongCount.value - 1)
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
        currentSong.value?.apply {
            this.favorite = value
            saveSong(this)
            if (!value && currentArtist.value == ARTIST_FAVORITE) {
                localStateHolder.currentSongCount.value = getCountByArtistUseCase.execute(
                    ARTIST_FAVORITE
                )
                if (currentSongCount.value > 0) {
                    if (currentSongPosition.value >= currentSongCount.value) {
                        selectSong(currentSongPosition.value - 1)
                    } else {
                        selectSong(currentSongPosition.value)
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
        localStateHolder.scrollPosition.value = position
    }

    fun updateOrientationWasChanged(value: Boolean) {
        localStateHolder.wasOrientationChanged.value = value
        if (value) updateNeedScroll(true)
    }

    fun updateNeedScroll(value: Boolean) {
        localStateHolder.needScroll.value = value
    }

    fun updateLastOrientationIsPortrait(value: Boolean) {
        localStateHolder.isLastOrientationPortrait.value = value
    }

    fun deleteCurrentToTrash() {
        currentSong.value?.let {
            deleteSongToTrashUseCase.execute(it)
            localStateHolder.currentSongCount.value = getCountByArtistUseCase
                .execute(currentArtist.value)
            if (currentSongCount.value > 0) {
                if (currentSongPosition.value >= currentSongCount.value) {
                    selectSong(currentSongPosition.value - 1)
                } else {
                    selectSong(currentSongPosition.value)
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
}

