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
import jatx.russianrocksongbook.commonviewmodel.CommonViewModel
import jatx.russianrocksongbook.commonviewmodel.UIAction
import jatx.russianrocksongbook.commonviewmodel.contracts.WarningSender
import jatx.russianrocksongbook.domain.models.music.Music
import jatx.russianrocksongbook.navigation.NavControllerHolder
import jatx.russianrocksongbook.navigation.ScreenVariant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted.Companion.Eagerly
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
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
) {
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

    val editorText = mutableStateOf("")

    val localState = combine(localStateHolder.localState, commonState) { local, common ->
            local.copy(commonState = common)
        }
        .stateIn(
            viewModelScope,
            Eagerly,
            localStateHolder.localState.value
        )

    private var showSongsJob: Job? = null
    private var selectSongJob: Job? = null
    private var getArtistsJob: Job? = null
    private var uploadSongDisposable: Disposable? = null

    override val currentMusic: Music?
        get() = localState.value.currentSong

    override val warningSender = object : WarningSender {
        override fun sendWarningImpl(comment: String) {
            localState.value.currentSong?.let {
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
    }


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

    override fun handleAction(action: UIAction) {
        when (action) {
            is SelectArtist -> selectArtist(action.artist)
            is ShowSongs -> showSongs(action.artist, action.passToSongWithTitle)
            is SelectSong -> selectSong(action.position)
            is NextSong -> nextSong()
            is PrevSong -> prevSong()
            is SaveSong -> saveSong(action.song)
            is SetFavorite -> setFavorite(action.favorite)
            is DeleteCurrentToTrash -> deleteCurrentToTrash()
            is SpeechRecognize -> speechRecognize(action.dontAskMore)
            is UploadCurrentToCloud -> uploadCurrentToCloud()
            is ReviewApp -> reviewApp()
            is ShowDevSite -> showDevSite()
            is UpdateArtists -> updateArtists()
            is UpdateCurrentSong -> updateCurrentSong(action.song, localState.value.currentSongPosition)
            is UpdateMenuScrollPosition -> updateMenuScrollPosition(action.position)
            is UpdateMenuExpandedArtistGroup -> updateMenuExpandedArtistGroup(action.artistGroup)
            is UpdateSongListScrollPosition -> updateSongListScrollPosition(action.position)
            is UpdateSongListNeedScroll -> updateSongListNeedScroll(action.need)
            is SetEditorMode -> setEditorMode(action.isEditor)
            is SetAutoPlayMode -> setAutoPlayMode(action.isAutoPlay)
            else -> super.handleAction(action)
        }
    }

    protected fun selectArtist(artist: String) {
        Log.e("select artist", artist)
        val newScreenVariant = when (artist) {
            ARTIST_ADD_ARTIST -> {
                ScreenVariant.AddArtist
            }
            ARTIST_ADD_SONG -> {
                ScreenVariant.AddSong
            }
            ARTIST_CLOUD_SONGS -> {
                ScreenVariant.CloudSearch()
            }
            ARTIST_DONATION -> {
                ScreenVariant.Donation
            }
            ARTIST_FAVORITE -> {
                ScreenVariant.Favorite(
                    isBackFromSong = false
                )
            }
            else -> {
                ScreenVariant.SongList(
                    artist = artist,
                    isBackFromSong = false
                )
            }
        }
        // for unit tests:
        if (artist !in listOf(
                ARTIST_ADD_ARTIST,
                ARTIST_ADD_SONG,
                ARTIST_CLOUD_SONGS,
                ARTIST_DONATION)
            && NavControllerHolder.navController == null) {

            showSongs(artist)
        } else {
            selectScreen(newScreenVariant)
        }
    }

    private fun showSongs(
        artist: String,
        passToSongWithTitle: String? = null
    ) {
        Log.e("show songs", artist)
        updateCurrentArtist(artist)
        updateCurrentSongCount(getCountByArtistUseCase.execute(artist))
        showSongsJob?.let {
            if (!it.isCancelled) it.cancel()
        }
        var _passToSongWithTitle = passToSongWithTitle
        showSongsJob = viewModelScope
            .launch {
                withContext(Dispatchers.IO) {
                    getSongsByArtistUseCase
                        .execute(artist)
                        .collect { songs ->
                            withContext(Dispatchers.Main) {
                                val oldArtist = localState.value.currentSongList.getOrNull(0)?.artist
                                    ?: "null"
                                val newArtist = songs.getOrNull(0)?.artist ?: "null"
                                if (newArtist == artist || newArtist == "null" || artist == ARTIST_FAVORITE) {
                                    updateCurrentSongList(songs)
                                    if (_passToSongWithTitle != null) {
                                        songs
                                            .map { it.title }
                                            .indexOf(_passToSongWithTitle)
                                            .takeIf { it >= 0 }
                                            ?.let { position ->
                                                selectScreen(
                                                    ScreenVariant
                                                        .SongText(newArtist, position))
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

    private fun selectSong(position: Int) {
        selectSongJob?.let {
            if (!it.isCancelled) it.cancel()
        }

        Log.e("select song", position.toString())

        val oldArtist = localState.value.currentSong?.artist
        val oldTitle = localState.value.currentSong?.title

        selectSongJob = viewModelScope
            .launch {
                withContext(Dispatchers.IO) {
                    getSongByArtistAndPositionUseCase
                        .execute(localState.value.currentArtist, position)
                        .collect { song ->
                            withContext(Dispatchers.Main) {
                                updateCurrentSong(song, position)

                                val newArtist = song?.artist
                                val newTitle = song?.title

                                if (newArtist != oldArtist || newTitle != oldTitle) {
                                    setAutoPlayMode(false)
                                    setEditorMode(false)
                                    song?.let {
                                        updateEditorText(it.text)
                                    }
                                }
                            }
                        }
                }
            }
    }

    private fun nextSong() {
        with (localState.value) {
            if (currentSongCount > 0) {
                selectScreen(
                    ScreenVariant
                        .SongText(
                            artist = currentArtist,
                            position = (currentSongPosition + 1) % currentSongCount
                        ))
            }
        }
    }

    private fun prevSong() {
        with (localState.value) {
            if (currentSongCount > 0) {
                if (currentSongPosition > 0) {
                    selectScreen(
                        ScreenVariant
                            .SongText(
                                artist = currentArtist,
                                position = (currentSongPosition - 1) % currentSongCount
                            )
                    )
                } else {
                    selectScreen(
                        ScreenVariant
                            .SongText(
                                artist = currentArtist,
                                position = currentSongCount - 1
                            )
                    )
                }
            }
        }
    }

    private fun saveSong(song: Song) {
        updateSongUseCase.execute(song)
    }

    private fun setFavorite(favorite: Boolean) {
        Log.e("set favorite", favorite.toString())
        with (localState.value) {
            currentSong?.copy(favorite = favorite)?.let {
                updateCurrentSong(it, localState.value.currentSongPosition)
                saveSong(it)
                if (!favorite && currentArtist == ARTIST_FAVORITE) {
                    val newSongCount = getCountByArtistUseCase.execute(ARTIST_FAVORITE)
                    updateCurrentSongCount(
                        newSongCount
                    )
                    if (newSongCount > 0) {
                        if (currentSongPosition >= newSongCount) {
                            selectScreen(
                                ScreenVariant
                                    .SongText(
                                        artist = currentArtist,
                                        position = currentSongPosition - 1
                                    )
                            )
                        } else {
                            selectScreen(
                                ScreenVariant
                                    .SongText(
                                        artist = currentArtist,
                                        position = currentSongPosition
                                    )
                            )
                        }
                    } else {
                        back()
                    }
                }
                if (favorite) {
                    showToast(R.string.toast_added_to_favorite)
                } else {
                    showToast(R.string.toast_removed_from_favorite)
                }
            }
        }
    }

    private fun deleteCurrentToTrash() {
        with (localState.value) {
            currentSong?.let {
                deleteSongToTrashUseCase.execute(it)
                val newSongCount = getCountByArtistUseCase.execute(currentArtist)
                updateCurrentSongCount(newSongCount)
                if (newSongCount > 0) {
                    if (currentSongPosition >= newSongCount) {
                        selectScreen(
                            ScreenVariant
                                .SongText(
                                    artist = currentArtist,
                                    position = currentSongPosition - 1
                                )
                        )
                    } else {
                        selectScreen(
                            ScreenVariant
                                .SongText(
                                    artist = currentArtist,
                                    position = currentSongPosition
                                )
                        )
                    }
                } else {
                    back()
                }
            }
            showToast(R.string.toast_deleted_to_trash)
        }
    }

    private fun speechRecognize(dontAskMore: Boolean) {
        settings.voiceHelpDontAsk = dontAskMore
        callbacks.onSpeechRecognize()
    }

    private fun uploadCurrentToCloud() {
        localState.value.currentSong?.let { song ->
            setUploadButtonEnabled(false)
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
                    setUploadButtonEnabled(true)
                }, { error ->
                    error.printStackTrace()
                    showToast(R.string.error_in_app)
                    setUploadButtonEnabled(true)
                })
        }
    }

    private fun reviewApp() {
        callbacks.onReviewApp()
    }

    private fun showDevSite() {
        callbacks.onShowDevSite()
    }

    private fun updateArtists() {
        getArtistsJob?.let {
            if (!it.isCancelled) it.cancel()
        }
        getArtistsJob = viewModelScope.launch {
            getArtistsUseCase
                .execute()
                .collect {
                    withContext(Dispatchers.Main) {
                        updateArtists(it)
                    }
                }
        }
    }

    private fun updateCurrentSong(song: Song?, position: Int) {
        localStateHolder.localState.update {
            it.copy(
                currentSong = song,
                currentSongPosition = position,
                songListScrollPosition = position,
                songListNeedScroll = true
            )
        }
    }

    private fun updateMenuScrollPosition(position: Int) {
        localStateHolder.localState.update {
            it.copy(menuScrollPosition = position)
        }
    }

    private fun updateMenuExpandedArtistGroup(artistGroup: String) {
        localStateHolder.localState.update {
            it.copy(menuExpandedArtistGroup = artistGroup)
        }
    }

    private fun updateSongListScrollPosition(position: Int) {
        localStateHolder.localState.update {
            it.copy(songListScrollPosition = position)
        }
    }

    private fun updateSongListNeedScroll(needScroll: Boolean) {
        localStateHolder.localState.update {
            it.copy(songListNeedScroll = needScroll)
        }
    }

    private fun setEditorMode(editorMode: Boolean) {
        localStateHolder.localState.update {
            it.copy(isEditorMode = editorMode)
        }
    }

    private fun setAutoPlayMode(autoPlayMode: Boolean) {
        localStateHolder.localState.update {
            it.copy(isAutoPlayMode = autoPlayMode)
        }
    }

    private fun updateArtists(artists: List<String>) {
        localStateHolder.commonStateHolder.commonState.update {
            it.copy(artistList = artists)
        }
    }

    private fun updateCurrentArtist(artist: String) {
        localStateHolder.commonStateHolder.commonState.update {
            it.copy(currentArtist = artist)
        }
    }

    private fun updateCurrentSongCount(count: Int) {
        localStateHolder.localState.update {
            it.copy(currentSongCount = count)
        }
    }

    private fun updateCurrentSongList(songList: List<Song>) {
        localStateHolder.localState.update {
            it.copy(currentSongList = songList)
        }
    }

    private fun setUploadButtonEnabled(enabled: Boolean) {
        localStateHolder.localState.update {
            it.copy(isUploadButtonEnabled = enabled)
        }
    }
    private fun updateEditorText(text: String) {
        editorText.value = text
    }
}
