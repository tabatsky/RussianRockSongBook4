package jatx.russianrocksongbook.textsearch.internal.viewmodel

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
import jatx.spinner.SpinnerState
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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

    val spinnerStateOrderBy = mutableStateOf(SpinnerState(0, false))

    val textSearchStateFlow = textSearchStateHolder.textSearchStateFlow.asStateFlow()

    override val currentMusic: Music?
        get() = textSearchStateFlow.value.currentSong

    override val currentWarnable: Warnable?
        get() = textSearchStateFlow.value.currentSong

    companion object {
        private const val key = "TextSearch"

        @Composable
        fun getInstance(): TextSearchViewModel {
            if (!storage.containsKey(key)){
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
//            is UpdateCurrentSong -> updateCurrentCloudSong(action.cloudSong)
//            is SelectSong -> selectCloudSong(action.position)
            is UpdateSongListScrollPosition -> updateScrollPosition(action.position)
            is UpdateSongListNeedScroll -> updateNeedScroll(action.needScroll)
//            is NextSong -> nextCloudSong()
//            is PrevSong -> prevCloudSong()
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
        textSearchStateHolder.textSearchStateFlow.update {
            it.copy(searchFor = searchFor)
        }
    }

    private fun updateOrderBy(orderBy: TextSearchOrderBy) {
        textSearchStateHolder.textSearchStateFlow.update {
            it.copy(orderBy = orderBy)
        }
    }

    private fun updateSongs(songs: List<Song>) {
        textSearchStateHolder.textSearchStateFlow.update {
            it.copy(songs = songs)
        }
    }

    private fun updateCurrentSongCount(count: Int) {
        textSearchStateHolder.textSearchStateFlow.update {
            it.copy(currentSongCount = count)
        }
    }

    private fun updateScrollPosition(position: Int) {
        textSearchStateHolder.textSearchStateFlow.update {
            it.copy(scrollPosition = position)
        }
    }

    private fun updateNeedScroll(needScroll: Boolean) {
        textSearchStateHolder.textSearchStateFlow.update {
            it.copy(needScroll = needScroll)
        }
    }
}