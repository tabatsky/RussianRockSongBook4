package jatx.russianrocksongbook.textsearch.internal.viewmodel

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jatx.russianrocksongbook.commonsongtext.viewmodel.CommonSongTextViewModel
import jatx.russianrocksongbook.commonview.viewmodel.DeleteCurrentToTrash
import jatx.russianrocksongbook.commonviewmodel.UIAction
import jatx.russianrocksongbook.domain.models.local.Song
import jatx.russianrocksongbook.domain.repository.local.TextSearchOrderBy
import jatx.russianrocksongbook.navigation.ScreenVariant
import jatx.russianrocksongbook.textsearch.R
import jatx.spinner.SpinnerState
import javax.inject.Inject

@HiltViewModel
class TextSearchViewModel @Inject constructor(
    private val textSearchStateHolder: TextSearchStateHolder,
    textSearchViewModelDeps: TextSearchViewModelDeps
): CommonSongTextViewModel(
    textSearchStateHolder.commonSongTextStateHolder,
    textSearchViewModelDeps.commonSongTextViewModelDeps
) {
    private val getSongsByTextSearchUseCase =
        textSearchViewModelDeps.getSongsByTextSearchUseCase

    val spinnerStateOrderBy = mutableStateOf(SpinnerState(0, false))

    val textSearchStateFlow = textSearchStateHolder.textSearchStateFlow

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
            is DeleteCurrentToTrash -> deleteCurrentToTrash()
            else -> super.handleAction(action)
        }
    }

    override fun getSongTextScreenVariant(position: Int) = ScreenVariant
        .TextSearchSongText(position = position)

    override fun selectSong(position: Int) {
        val oldArtist = commonSongTextStateFlow.value.currentSong?.artist
        val oldTitle = commonSongTextStateFlow.value.currentSong?.title

        val songs = textSearchStateFlow.value.songs
        val song = songs.getOrNull(position) ?: return

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

    override fun setFavorite(favorite: Boolean) {
        Log.e("set favorite", favorite.toString())
        with(commonSongTextStateFlow.value) {
            currentSong?.copy(favorite = favorite)?.let {
                saveSong(it)
                if (favorite) {
                    showToast(R.string.toast_added_to_favorite)
                } else {
                    showToast(R.string.toast_removed_from_favorite)
                }
            }
        }
    }

    override fun deleteCurrentToTrash() {
        commonSongTextStateFlow.value.currentSong?.let {
            deleteSongToTrashUseCase.execute(it)
        }
        refreshSongs()
        with(commonSongTextStateFlow.value) {
            val newSongCount = textSearchStateFlow.value.songs.size
            updateCurrentSongCount(newSongCount)
            if (newSongCount > 0) {
                if (currentSongPosition >= newSongCount) {
                    selectScreen(getSongTextScreenVariant(
                        position = currentSongPosition - 1))
                } else {
                    selectScreen(getSongTextScreenVariant(
                        position = currentSongPosition
                    ))
                    refreshCurrentSong()
                }
            } else {
                back()
            }
        }
        showToast(R.string.toast_deleted_to_trash)
    }

    override fun saveSong(song: Song) {
        updateSongUseCase.execute(song)
        refreshSongs()
        refreshCurrentSong()
    }

    private fun performTextSearch(searchFor: String, orderBy: TextSearchOrderBy) {
        updateSongListScrollPosition(0)
        updateSongListNeedScroll(true)
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

    private fun refreshSongs() {
        with(textSearchStateFlow.value) {
            val words = searchFor.trim().split(" ")
            val songs = getSongsByTextSearchUseCase.execute(words, orderBy)
            updateSongs(songs)
        }
    }

    private fun updateSongs(songs: List<Song>) {
        textSearchStateHolder.textSearchStateFlow.value.let {
            changeTextSearchState(it.copy(songs = songs))
        }
    }

    private fun refreshCurrentSong() {
        val updatedSong = textSearchStateFlow.value.songs[
            commonSongTextStateFlow.value.currentSongPosition]
        updateCurrentSong(updatedSong)
    }

    private fun changeTextSearchState(newState: TextSearchState) =
        textSearchStateHolder.changeTextSearchState(newState)
}