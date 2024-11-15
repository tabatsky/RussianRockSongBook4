package jatx.russianrocksongbook.cloudsongs.internal.view.cloudsearch

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.paging.compose.collectAsLazyPagingItems
import jatx.russianrocksongbook.cloudsongs.internal.viewmodel.CloudViewModel

@Composable
internal fun CloudSearchScreenImpl(randomKey: Int, isBackFromSong: Boolean) {
    val cloudViewModel = CloudViewModel.getInstance()
    val cloudState by cloudViewModel.cloudStateFlow.collectAsState()

    val searchState = cloudState.searchState

    val needScroll = cloudState.needScroll
    val scrollPosition = cloudState.scrollPosition

    val cloudSongsFlow = cloudState.cloudSongsFlow
    val cloudSongItems = cloudSongsFlow?.collectAsLazyPagingItems()

    val searchFor = cloudState.searchFor
    val orderBy = cloudState.orderBy

    val spinnerStateOrderBy = cloudViewModel.spinnerStateOrderBy
    val submitAction = cloudViewModel::submitAction

    CloudSearchScreenImplContent(
        randomKey = randomKey,
        isBackFromSong = isBackFromSong,
        searchState = searchState,
        needScroll = needScroll,
        scrollPosition = scrollPosition,
        cloudSongItems = cloudSongItems,
        searchFor = searchFor,
        orderBy = orderBy,
        spinnerStateOrderBy = spinnerStateOrderBy,
        submitAction = submitAction
    )
}
