package jatx.russianrocksongbook.localsongs.internal.viewmodel

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jatx.russianrocksongbook.domain.models.local.Song
import jatx.russianrocksongbook.domain.repository.cloud.result.STATUS_ERROR
import jatx.russianrocksongbook.domain.repository.cloud.result.STATUS_SUCCESS
import jatx.russianrocksongbook.domain.repository.local.*
import jatx.russianrocksongbook.localsongs.R
import jatx.russianrocksongbook.commonviewmodel.CommonViewModel
import jatx.russianrocksongbook.commonviewmodel.ShowSongs
import jatx.russianrocksongbook.commonviewmodel.UIAction
import jatx.russianrocksongbook.domain.models.music.Music
import jatx.russianrocksongbook.domain.models.warning.Warnable
import jatx.russianrocksongbook.navigation.AppNavigator
import jatx.russianrocksongbook.navigation.ScreenVariant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
open class LocalViewModel @Inject constructor(
    private val localStateHolder: LocalStateHolder,
    localViewModelDeps: LocalViewModelDeps
): CommonViewModel(
    localStateHolder.appStateHolder,
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
    private val addSongToCloudUseCase =
        localViewModelDeps.addSongToCloudUseCase
    private val getArtistsUseCase =
        localViewModelDeps.getArtistsUseCase

    val editorText = mutableStateOf("")

    val localStateFlow by lazy {
        localStateHolder.localStateFlow
    }

    private var showSongsJob: Job? = null
    private var selectSongJob: Job? = null
    private var getArtistsJob: Job? = null
    private var uploadSongJob: Job? = null

    override val currentMusic: Music?
        get() = localStateFlow.value.currentSong

    override val currentWarnable: Warnable?
        get() = localStateFlow.value.currentSong

    companion object {
        private const val key = "Local"

        @Composable
        fun getInstance(): LocalViewModel {
            if (!storage.containsKey(key)){
                storage[key] = hiltViewModel<LocalViewModel>()
            }
            storage[key]?.launchJobsIfNecessary()
            return (storage[key] as LocalViewModel)
        }

        fun getStoredInstance() = storage[key] as? LocalViewModel
    }

    override fun resetState() = localStateHolder.reset()

    override fun handleAction(action: UIAction) {
        when (action) {
            is SelectArtist -> selectArtist(action.artist)
            is ShowSongs -> showSongs(action.artist, action.songTitleToPass)
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
            is UpdateCurrentSong -> updateCurrentSong(action.song, localStateFlow.value.currentSongPosition)
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
                    isBackFromSomeScreen = false
                )
            }
            else -> {
                ScreenVariant.SongList(
                    artist = artist,
                    isBackFromSomeScreen = false
                )
            }
        }

        if (artist !in listOf(
                ARTIST_ADD_ARTIST,
                ARTIST_ADD_SONG,
                ARTIST_CLOUD_SONGS,
                ARTIST_DONATION)
            && AppNavigator.navControllerIsNull) {

            showSongs(artist)
        } else {
            selectScreen(newScreenVariant)
        }
    }

    private fun showSongs(
        artist: String,
        songTitleToPass: String? = null
    ) {
        Log.e("show songs", artist)
        updateCurrentArtist(artist)
        updateCurrentSongCount(getCountByArtistUseCase.execute(artist))
        showSongsJob?.let {
            if (!it.isCancelled) it.cancel()
        }
        showSongsJob = viewModelScope
            .launch {
                withContext(Dispatchers.IO) {
                    getSongsByArtistUseCase
                        .execute(artist)
                        .collect { songs ->
                            withContext(Dispatchers.Main) {
                                val oldArtist = localStateFlow.value.currentSongList.getOrNull(0)?.artist
                                    ?: "null"
                                val newArtist = songs.getOrNull(0)?.artist ?: "null"
                                if (newArtist == artist || newArtist == "null" || artist == ARTIST_FAVORITE) {
                                    updateCurrentSongList(songs)
                                    songTitleToPass?.let {
                                        passToSongWithTitle(songs, artist, it)
                                    } ?: run {
                                        Log.e("first song artist", "was: $oldArtist; become: $newArtist")
                                        if ((oldArtist != newArtist || artist == ARTIST_FAVORITE)
                                            && newArtist != "null") {

                                            selectSong(0)
                                        }
                                    }
                                }
                            }
                        }
                }
            }
    }

    private fun passToSongWithTitle(songs: List<Song>, artist: String, songTitleToPass: String) {
        Log.e("pass to song", "$artist - $songTitleToPass")
        songs
            .map { it.title }
            .indexOf(songTitleToPass)
            .takeIf { it >= 0 }
            ?.let { position ->
                selectScreen(
                    ScreenVariant
                        .SongList(artist)
                )
                selectScreen(
                    ScreenVariant
                        .SongText(artist, position))
            }
    }

    private fun selectSong(position: Int) {
        selectSongJob?.let {
            if (!it.isCancelled) it.cancel()
        }

        Log.e("select song", position.toString())

        val oldArtist = localStateFlow.value.currentSong?.artist
        val oldTitle = localStateFlow.value.currentSong?.title

        Log.e("select song", "oldArtist: $oldArtist")
        Log.e("select song", "oldTitle: $oldTitle")

        val currentArtist = appStateFlow.value.currentArtist
        Log.e("select song", "currentArtist: $currentArtist")

        selectSongJob = viewModelScope
            .launch {
                withContext(Dispatchers.IO) {
                    getSongByArtistAndPositionUseCase
                        .execute(currentArtist, position)
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
        val currentArtist = appStateFlow.value.currentArtist
        with (localStateFlow.value) {
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
        val currentArtist = appStateFlow.value.currentArtist
        with (localStateFlow.value) {
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
        val currentArtist = appStateFlow.value.currentArtist
        with (localStateFlow.value) {
            currentSong?.copy(favorite = favorite)?.let {
                updateCurrentSong(it, currentSongPosition)
                saveSong(it)
                if (!favorite && currentArtist == ARTIST_FAVORITE) {
                    val newSongCount = getCountByArtistUseCase.execute(ARTIST_FAVORITE)
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
                if (favorite) {
                    showToast(R.string.toast_added_to_favorite)
                } else {
                    showToast(R.string.toast_removed_from_favorite)
                }
            }
        }
    }

    private fun deleteCurrentToTrash() {
        val currentArtist = appStateFlow.value.currentArtist
        with (localStateFlow.value) {
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
        localStateFlow.value.currentSong?.let { song ->
            setUploadButtonEnabled(false)
            uploadSongJob?.let {
                if (!it.isCancelled) it.cancel()
            }
            uploadSongJob = viewModelScope.launch {
                withContext(Dispatchers.Main) {
                    try {
                        val result = withContext(Dispatchers.IO) {
                            addSongToCloudUseCase.execute(song)
                        }
                        when (result.status) {
                            STATUS_SUCCESS -> showToast(R.string.toast_upload_to_cloud_success)
                            STATUS_ERROR -> showToast(result.message ?: "")
                        }
                        setUploadButtonEnabled(true)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        showToast(R.string.error_in_app)
                        setUploadButtonEnabled(true)
                    }
                }
            }
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
        val localState = localStateFlow.value
        val newState = localState.copy(
            currentSong = song,
            currentSongPosition = position,
            songListScrollPosition = position,
            songListNeedScroll = true
        )
        changeLocalState(newState)
    }

    private fun updateMenuScrollPosition(position: Int) {
        val localState = localStateFlow.value
        val newState = localState.copy(menuScrollPosition = position)
        changeLocalState(newState)
    }

    private fun updateMenuExpandedArtistGroup(artistGroup: String) {
        val localState = localStateFlow.value
        val newState = localState.copy(menuExpandedArtistGroup = artistGroup)
        changeLocalState(newState)
    }

    private fun updateSongListScrollPosition(position: Int) {
        val localState = localStateFlow.value
        val newState = localState.copy(songListScrollPosition = position)
        changeLocalState(newState)
    }

    private fun updateSongListNeedScroll(needScroll: Boolean) {
        val localState = localStateFlow.value
        val newState = localState.copy(songListNeedScroll = needScroll)
        changeLocalState(newState)
    }

    private fun setEditorMode(editorMode: Boolean) {
        val localState = localStateFlow.value
        val newState = localState.copy(isEditorMode = editorMode)
        changeLocalState(newState)
    }

    private fun setAutoPlayMode(autoPlayMode: Boolean) {
        val localState = localStateFlow.value
        val newState = localState.copy(isAutoPlayMode = autoPlayMode)
        changeLocalState(newState)
    }

    private fun updateCurrentSongCount(count: Int) {
        val localState = localStateFlow.value
        val newState = localState.copy(currentSongCount = count)
        changeLocalState(newState)
    }

    private fun updateCurrentSongList(songList: List<Song>) {
        val localState = localStateFlow.value
        val newState = localState.copy(currentSongList = songList)
        changeLocalState(newState)
    }

    private fun setUploadButtonEnabled(enabled: Boolean) {
        val localState = localStateFlow.value
        val newState = localState.copy(isUploadButtonEnabled = enabled)
        changeLocalState(newState)
    }

    private fun changeLocalState(localState: LocalState) =
        localStateHolder.changeLocalState(localState)

    private fun updateArtists(artists: List<String>) {
        val appState = appStateFlow.value
        val newState = appState.copy(artistList = artists)
        changeAppState(newState)
    }

    private fun updateCurrentArtist(artist: String) {
        val appState = appStateFlow.value
        val newState = appState.copy(currentArtist = artist)
        changeAppState(newState)
    }

    private fun updateEditorText(text: String) {
        editorText.value = text
    }
}
