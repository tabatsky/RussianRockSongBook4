package jatx.russianrocksongbook.textsearch.internal.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jatx.russianrocksongbook.commonviewmodel.CommonViewModel
import jatx.russianrocksongbook.commonviewmodel.UIAction
import jatx.russianrocksongbook.domain.models.music.Music
import jatx.russianrocksongbook.domain.models.warning.Warnable
import jatx.russianrocksongbook.domain.repository.cloud.OrderBy
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
//            is UpdateCurrentSongCount -> updateCurrentCloudSongCount(action.count)
//            is UpdateCurrentSong -> updateCurrentCloudSong(action.cloudSong)
//            is SelectSong -> selectCloudSong(action.position)
//            is UpdateSongListScrollPosition -> updateScrollPosition(action.position)
//            is UpdateSongListNeedScroll -> updateNeedScroll(action.needScroll)
//            is NextSong -> nextCloudSong()
//            is PrevSong -> prevCloudSong()
            else -> super.handleAction(action)
        }
    }

    private fun performTextSearch(searchFor: String, orderBy: OrderBy) {
//        updateScrollPosition(0)
//        updateNeedScroll(true)
        updateSearchFor(searchFor)
        updateOrderBy(orderBy)
    }

    private fun updateSearchFor(searchFor: String) {
        textSearchStateHolder.textSearchStateFlow.update {
            it.copy(searchFor = searchFor)
        }
    }

    private fun updateOrderBy(orderBy: OrderBy) {
        textSearchStateHolder.textSearchStateFlow.update {
            it.copy(orderBy = orderBy)
        }
    }
}