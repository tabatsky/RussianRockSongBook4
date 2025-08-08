package jatx.russianrocksongbook.localsongs.internal.viewmodel

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jatx.russianrocksongbook.commonsongtext.R
import jatx.russianrocksongbook.commonsongtext.viewmodel.CommonSongTextViewModel
import jatx.russianrocksongbook.domain.models.local.Song
import jatx.russianrocksongbook.domain.repository.local.*
import jatx.russianrocksongbook.commonviewmodel.ShowSongs
import jatx.russianrocksongbook.commonviewmodel.UIAction
import jatx.russianrocksongbook.navigation.*
import jatx.russianrocksongbook.testing.TestingConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
open class LocalViewModel @Inject constructor(
    private val localStateHolder: LocalStateHolder,
    localViewModelDeps: LocalViewModelDeps
): CommonSongTextViewModel(
    localStateHolder.commonSongTextStateHolder,
    localViewModelDeps.commonSongTextViewModelDeps
) {
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

    private val getSongsByArtistUseCase =
        localViewModelDeps.getSongsByArtistUseCase
    private val getArtistsUseCase =
        localViewModelDeps.getArtistsUseCase

    val localStateFlow by lazy {
        localStateHolder.localStateFlow
    }

    private var showSongsJob: Job? = null
    private var getArtistsJob: Job? = null

    private var selectSongJob: Job? = null

    override fun resetState() = localStateHolder.reset()

    override fun handleAction(action: UIAction) {
        when (action) {
            is SelectArtist -> selectArtist(action.artist)
            is ShowSongs -> showSongs(action.artist, action.songTitleToPass)
            is SpeechRecognize -> speechRecognize(action.dontAskMore)
            is ReviewApp -> reviewApp()
            is ShowDevSite -> showDevSite()
            is UpdateArtists -> updateArtists()
            is UpdateMenuScrollPosition -> updateMenuScrollPosition(action.position)
            is UpdateMenuExpandedArtistGroup -> updateMenuExpandedArtistGroup(action.artistGroup)
            else -> super.handleAction(action)
        }
    }

    override fun getSongTextScreenVariant(position: Int) = SongTextScreenVariant(
            position = position
        )

    override fun selectSong(position: Int) {
        selectSongJob?.let {
            if (!it.isCancelled) it.cancel()
        }

        Log.e("select song", position.toString())

        val oldArtist = commonSongTextStateFlow.value.currentSong?.artist
        val oldTitle = commonSongTextStateFlow.value.currentSong?.title

        Log.e("select song", "oldArtist: $oldArtist")
        Log.e("select song", "oldTitle: $oldTitle")

        val currentArtist = appStateFlow.value.currentArtist
        Log.e("select song", "currentArtist: $currentArtist")

        selectSongJob = viewModelScope
            .launch {
                withContext(Dispatchers.IO) {
                    getSongByArtistAndPositionUseCase
                        .execute(currentArtist, position)
                        .let { song ->
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

    override fun setFavorite(favorite: Boolean) {
        Log.e("set favorite", favorite.toString())
        val currentArtist = appStateFlow.value.currentArtist
        with (commonSongTextStateFlow.value) {
            currentSong?.copy(favorite = favorite)?.let {
                saveSong(it)
                if (!favorite && currentArtist == ARTIST_FAVORITE) {
                    val newSongCount = getCountByArtistUseCase.execute(ARTIST_FAVORITE)
                    updateCurrentSongCount(newSongCount)
                    if (newSongCount > 0) {
                        if (currentSongPosition >= newSongCount) {
                            selectScreen(getSongTextScreenVariant(
                                position = currentSongPosition - 1))
                        } else {
                            selectScreen(getSongTextScreenVariant(
                                position = currentSongPosition))
                        }
                    } else {
                        back()
                    }
                } else {
                    updateCurrentSong(it, currentSongPosition)
                }
                if (favorite) {
                    showToast(R.string.toast_added_to_favorite)
                } else {
                    showToast(R.string.toast_removed_from_favorite)
                }
            }
        }
    }

    override fun deleteCurrentToTrash() {
        val currentArtist = appStateFlow.value.currentArtist
        with (commonSongTextStateFlow.value) {
            currentSong?.let {
                deleteSongToTrashUseCase.execute(it)
                val newSongCount = getCountByArtistUseCase.execute(currentArtist)
                updateCurrentSongCount(newSongCount)
                if (newSongCount > 0) {
                    if (currentSongPosition >= newSongCount) {
                        selectScreen(getSongTextScreenVariant(position = currentSongPosition - 1))
                    } else {
                        selectScreen(getSongTextScreenVariant(
                            position = currentSongPosition))
                    }
                } else {
                    back()
                }
            }
            showToast(R.string.toast_deleted_to_trash)
        }
    }

    override fun saveSong(song: Song) {
        updateSongUseCase.execute(song)
    }

    protected fun selectArtist(artist: String) {
        Log.e("select artist", artist)
        val newScreenVariant = when (artist) {
            ARTIST_ADD_ARTIST -> {
                AddArtistScreenVariant
            }
            ARTIST_ADD_SONG -> {
                AddSongScreenVariant
            }
            ARTIST_CLOUD_SONGS -> {
                CloudSearchScreenVariant()
            }
            ARTIST_TEXT_SEARCH -> {
                TextSearchListScreenVariant()
            }
            ARTIST_DONATION -> {
                DonationScreenVariant
            }
            ARTIST_FAVORITE -> {
                FavoriteScreenVariant(
                    isBackFromSomeScreen = false
                )
            }
            else -> {
                SongListScreenVariant(
                    artist = artist,
                    isBackFromSomeScreen = false
                )
            }
        }

        selectScreen(newScreenVariant)

        if (artist !in listOf(
                ARTIST_ADD_ARTIST,
                ARTIST_ADD_SONG,
                ARTIST_CLOUD_SONGS,
                ARTIST_TEXT_SEARCH,
                ARTIST_DONATION)
            && TestingConfig.isUnitTesting) {
            showSongs(artist)
        }
    }

    private fun showSongs(
        artist: String,
        songTitleToPass: String? = null
    ) {
        Log.e("show songs", artist)
        settings.defaultArtist = artist
        if (artist != appStateFlow.value.currentArtist) updateCurrentSongList(listOf())
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
                        .let { songs ->
                            withContext(Dispatchers.Main) {
                                val oldArtist = localStateFlow.value.currentSongList.getOrNull(0)?.artist
                                val newArtist = songs.getOrNull(0)?.artist
                                Log.e("new artist", newArtist ?: "null")
                                if (newArtist == artist || newArtist == null || artist == ARTIST_FAVORITE) {
                                    updateCurrentSongList(songs)
                                    songTitleToPass?.let {
                                        passToSongWithTitle(songs, artist, it)
                                    } ?: run {
                                        Log.e("first song artist", "was: $oldArtist; become: $newArtist")
                                        if (oldArtist == null && newArtist != null) {
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
                    SongListScreenVariant(artist)
                )
                selectScreen(
                    SongTextScreenVariant(position))
            }
    }

    private fun speechRecognize(dontAskMore: Boolean) {
        settings.voiceHelpDontAsk = dontAskMore
        callbacks.onSpeechRecognize()
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

    private fun updateCurrentSongList(songList: List<Song>) {
        val localState = localStateFlow.value
        val newState = localState.copy(currentSongList = songList)
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
}
