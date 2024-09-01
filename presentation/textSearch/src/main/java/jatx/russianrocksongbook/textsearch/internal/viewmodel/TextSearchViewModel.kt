package jatx.russianrocksongbook.textsearch.internal.viewmodel

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jatx.russianrocksongbook.commonviewmodel.CommonViewModel
import jatx.russianrocksongbook.commonviewmodel.UIAction
import jatx.russianrocksongbook.domain.models.local.Song
import jatx.russianrocksongbook.domain.models.music.Music
import jatx.russianrocksongbook.domain.models.warning.Warnable
import jatx.russianrocksongbook.domain.repository.local.TextSearchOrderBy
import jatx.russianrocksongbook.navigation.ScreenVariant
import jatx.russianrocksongbook.textsearch.R
import jatx.spinner.SpinnerState
import javax.inject.Inject

@HiltViewModel
class TextSearchViewModel @Inject constructor(
    private val textSearchStateHolder: TextSearchStateHolder,
    textSearchViewModelDeps: CloudViewModelDeps
): CommonViewModel(
    textSearchStateHolder.appStateHolder,
    textSearchViewModelDeps.commonViewModelDeps
) {
    private val getSongsByTextSearchUseCase =
        textSearchViewModelDeps.getSongsByTextSearchUseCase
    private val updateSongUseCase =
        textSearchViewModelDeps.updateSongUseCase
    private val deleteSongToTrashUseCase =
        textSearchViewModelDeps.deleteSongToTrashUseCase

    val editorText = mutableStateOf("")

    val spinnerStateOrderBy = mutableStateOf(SpinnerState(0, false))

    val textSearchStateFlow = textSearchStateHolder.textSearchStateFlow

    override val currentMusic: Music?
        get() = textSearchStateFlow.value.currentSong

    override val currentWarnable: Warnable?
        get() = textSearchStateFlow.value.currentSong

    companion object {
        private const val key = "TextSearch"

        @Composable
        fun getInstance(): TextSearchViewModel {
            if (!storage.containsKey(key)) {
                storage[key] = hiltViewModel<TextSearchViewModel>()
            }
            storage[key]?.launchJobsIfNecessary()
            return storage[key] as TextSearchViewModel
        }
    }

    override fun resetState() = textSearchStateHolder.reset()

    override fun handleAction(action: UIAction) {
        when (action) {
            is PerformTextSearch -> performTextSearch(action.searchFor, action.orderBy)
            is UpdateSearchFor -> updateSearchFor(action.searchFor)
            is UpdateOrderBy -> updateOrderBy(action.orderBy)
            is UpdateCurrentSongCount -> updateCurrentSongCount(action.count)
            is UpdateCurrentSong -> updateCurrentSong(action.song)
            is SelectSong -> selectSong(action.position)
            is UpdateSongListScrollPosition -> updateScrollPosition(action.position)
            is UpdateSongListNeedScroll -> updateNeedScroll(action.needScroll)
            is NextSong -> nextSong()
            is PrevSong -> prevSong()
            is SetEditorMode -> setEditorMode(action.isEditor)
            is SetAutoPlayMode -> setAutoPlayMode(action.isAutoPlay)
            is SaveSong -> saveSong(action.song)
            is SetFavorite -> setFavorite(action.favorite)
            is UploadCurrentToCloud -> uploadCurrentToCloud()
            is DeleteCurrentToTrash -> deleteCurrentToTrash()
            else -> super.handleAction(action)
        }
    }

    private fun performTextSearch(searchFor: String, orderBy: TextSearchOrderBy) {
        updateScrollPosition(0)
        updateNeedScroll(true)
        updateSearchFor(searchFor)
        updateOrderBy(orderBy)
        val words = searchFor.trim().split(" ")
        val songs = getSongsByTextSearchUseCase.execute(words, orderBy)
        updateSongs(songs)
        updateCurrentSongCount(songs.size)
    }

    private fun updateSearchFor(searchFor: String) {
        textSearchStateHolder.textSearchStateFlow.value.let {
            changeTextSearchState(it.copy(searchFor = searchFor))
        }
    }

    private fun updateOrderBy(orderBy: TextSearchOrderBy) {
        textSearchStateHolder.textSearchStateFlow.value.let {
            changeTextSearchState(it.copy(orderBy = orderBy))
        }
    }

    private fun updateSongs(songs: List<Song>) {
        textSearchStateHolder.textSearchStateFlow.value.let {
            changeTextSearchState(it.copy(songs = songs))
        }
    }

    private fun updateCurrentSongCount(count: Int) {
        textSearchStateHolder.textSearchStateFlow.value.let {
            changeTextSearchState(it.copy(currentSongCount = count))
        }
    }

    private fun updateScrollPosition(position: Int) {
        textSearchStateHolder.textSearchStateFlow.value.let {
            changeTextSearchState(it.copy(scrollPosition = position))
        }
    }

    private fun updateNeedScroll(needScroll: Boolean) {
        textSearchStateHolder.textSearchStateFlow.value.let {
            changeTextSearchState(it.copy(needScroll = needScroll))
        }
    }

    private fun updateCurrentSong(song: Song?) {
        val localState = textSearchStateFlow.value
        val newState = localState.copy(
            currentSong = song
        )
        changeTextSearchState(newState)
    }

    private fun updateCurrentSongPosition(position: Int) {
        val localState = textSearchStateFlow.value
        val newState = localState.copy(
            currentSongPosition = position,
            scrollPosition = position,
            needScroll = true
        )
        changeTextSearchState(newState)
    }

    private fun selectSong(position: Int) {
        val oldArtist = textSearchStateFlow.value.currentSong?.artist
        val oldTitle = textSearchStateFlow.value.currentSong?.title

        val songs = textSearchStateFlow.value.songs
        val song = songs[position]

        updateCurrentSong(song)
        updateCurrentSongPosition(position)

        val newArtist = song.artist
        val newTitle = song.title

        if (newArtist != oldArtist || newTitle != oldTitle) {
            setAutoPlayMode(false)
            setEditorMode(false)
            updateEditorText(song.text)
        }
    }

    private fun nextSong() {
        with(textSearchStateFlow.value) {
            if (currentSongCount > 0) {
                selectScreen(
                    ScreenVariant
                        .TextSearchSongText(
                            position = (currentSongPosition + 1) % currentSongCount
                        )
                )
            }
        }
    }

    private fun prevSong() {
        with(textSearchStateFlow.value) {
            if (currentSongCount > 0) {
                if (currentSongPosition > 0) {
                    selectScreen(
                        ScreenVariant
                            .TextSearchSongText(
                                position = (currentSongPosition - 1) % currentSongCount
                            )
                    )
                } else {
                    selectScreen(
                        ScreenVariant
                            .TextSearchSongText(
                                position = currentSongCount - 1
                            )
                    )
                }
            }
        }
    }

    private fun setEditorMode(editorMode: Boolean) {
        val localState = textSearchStateFlow.value
        val newState = localState.copy(isEditorMode = editorMode)
        changeTextSearchState(newState)
    }

    private fun setAutoPlayMode(autoPlayMode: Boolean) {
        val localState = textSearchStateFlow.value
        val newState = localState.copy(isAutoPlayMode = autoPlayMode)
        changeTextSearchState(newState)
    }

    private fun setFavorite(favorite: Boolean) {
        Log.e("set favorite", favorite.toString())
        with(textSearchStateFlow.value) {
            currentSong?.copy(favorite = favorite)?.let {
                updateCurrentSong(it)
                saveSong(it)
                if (favorite) {
                    showToast(R.string.toast_added_to_favorite)
                } else {
                    showToast(R.string.toast_removed_from_favorite)
                }
            }
        }
    }

    private fun saveSong(song: Song) {
        updateSongUseCase.execute(song)
        refreshSongs()
        refreshCurrentSong()
    }

    private fun refreshSongs() {
        with(textSearchStateFlow.value) {
            val words = searchFor.trim().split(" ")
            val songs = getSongsByTextSearchUseCase.execute(words, orderBy)
            updateSongs(songs)
        }
    }

    private fun refreshCurrentSong() {
        with(textSearchStateFlow.value) {
            val updatedSong = songs[currentSongPosition]
            updateCurrentSong(updatedSong)
        }
    }

    private fun uploadCurrentToCloud() {
        textSearchStateFlow.value.currentSong?.let { song ->
            uploadSongToCloud(song) {
                setUploadButtonEnabled(it)
            }
        }
    }

    private fun setUploadButtonEnabled(enabled: Boolean) {
        val localState = textSearchStateFlow.value
        val newState = localState.copy(isUploadButtonEnabled = enabled)
        changeTextSearchState(newState)
    }

    private fun deleteCurrentToTrash() {
        textSearchStateFlow.value.currentSong?.let {
            deleteSongToTrashUseCase.execute(it)
        }
        refreshSongs()
        with(textSearchStateFlow.value) {
            val newSongCount = songs.size
            updateCurrentSongCount(newSongCount)
            if (newSongCount > 0) {
                if (currentSongPosition >= newSongCount) {
                    selectScreen(
                        ScreenVariant
                            .TextSearchSongText(
                                position = currentSongPosition - 1
                            )
                    )
                } else {
                    selectScreen(
                        ScreenVariant
                            .TextSearchSongText(
                                position = currentSongPosition
                            )
                    )
                    refreshCurrentSong()
                }
            } else {
                back()
            }
        }
        showToast(R.string.toast_deleted_to_trash)
    }

    private fun changeTextSearchState(newState: TextSearchState) =
        textSearchStateHolder.changeTextSearchState(newState)

    private fun updateEditorText(text: String) {
        editorText.value = text
    }
}