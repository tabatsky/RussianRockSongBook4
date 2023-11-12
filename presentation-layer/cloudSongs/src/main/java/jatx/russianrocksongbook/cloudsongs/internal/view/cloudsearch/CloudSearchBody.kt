package jatx.russianrocksongbook.cloudsongs.internal.view.cloudsearch

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.paging.compose.collectAsLazyPagingItems
import jatx.russianrocksongbook.cloudsongs.R
import jatx.russianrocksongbook.cloudsongs.internal.paging.ItemsAdapter
import jatx.russianrocksongbook.cloudsongs.internal.viewmodel.PerformCloudSearch
import jatx.russianrocksongbook.cloudsongs.internal.viewmodel.CloudViewModel
import jatx.russianrocksongbook.cloudsongs.internal.viewmodel.SearchState
import jatx.russianrocksongbook.cloudsongs.internal.viewmodel.UpdateNeedScroll
import jatx.russianrocksongbook.cloudsongs.internal.viewmodel.UpdateOrderBy
import jatx.russianrocksongbook.cloudsongs.internal.viewmodel.UpdateScrollPosition
import jatx.russianrocksongbook.cloudsongs.internal.viewmodel.UpdateSearchFor
import jatx.russianrocksongbook.cloudsongs.internal.viewmodel.UpdateSearchState
import jatx.russianrocksongbook.commonview.font.toScaledSp
import jatx.russianrocksongbook.commonview.stub.CommonSongListStub
import jatx.russianrocksongbook.commonview.stub.ErrorSongListStub
import jatx.russianrocksongbook.commonviewmodel.SelectScreen
import jatx.russianrocksongbook.domain.models.cloud.CloudSong
import jatx.russianrocksongbook.domain.repository.cloud.OrderBy
import jatx.russianrocksongbook.domain.repository.preferences.ScalePow
import jatx.russianrocksongbook.testing.TestingConfig
import jatx.russianrocksongbook.navigation.ScreenVariant
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@Composable
internal fun CloudSearchBody(
    modifier: Modifier,
    isPortrait: Boolean
) {
    val cloudViewModel = CloudViewModel.getInstance()
    val theme = cloudViewModel.theme.collectAsState().value

    val cloudState by cloudViewModel.cloudState.collectAsState()

    val searchState = cloudState.searchState

    val needScroll = cloudState.needScroll
    val scrollPosition = cloudState.scrollPosition

    val cloudSongsFlow = cloudState.cloudSongsFlow

    val cloudSongItems = cloudSongsFlow?.collectAsLazyPagingItems()
    val itemsAdapter = ItemsAdapter(cloudSongItems)

    val fontSizeTextSp = dimensionResource(id = R.dimen.text_size_16)
        .toScaledSp(cloudViewModel.fontScaler, ScalePow.TEXT)
    val fontSizeSongTitleSp = dimensionResource(id = R.dimen.text_size_20)
        .toScaledSp(cloudViewModel.fontScaler, ScalePow.TEXT)
    val fontSizeArtistSp = dimensionResource(id = R.dimen.text_size_24)
        .toScaledSp(cloudViewModel.fontScaler, ScalePow.TEXT)

    val searchFor = cloudState.searchFor
    val onSearchForValueChange: (String) -> Unit = {
        cloudViewModel.submitAction(UpdateSearchFor(it))
    }

    val orderBy = cloudState.orderBy
    val onOrderByValueChange: (OrderBy) -> Unit = {
        if (orderBy != it) {
            cloudViewModel.submitAction(UpdateOrderBy(it))
            cloudViewModel.submitAction(PerformCloudSearch(searchFor, it))
        }
    }

    val onSearchClick = {
        cloudViewModel.submitAction(PerformCloudSearch(searchFor, orderBy))
    }

    val onItemClick: (Int, CloudSong) -> Unit = { index, cloudSong ->
        println("selected: ${cloudSong.artist} - ${cloudSong.title}")
        cloudViewModel.submitAction(SelectScreen(ScreenVariant.CloudSongText(index)))
    }

    Column(
        modifier = modifier
    ) {
        if (isPortrait) {
            CloudSearchPanelPortrait(
                searchFor = searchFor,
                orderBy = orderBy,
                theme = theme,
                fontSizeTextSp = fontSizeTextSp,
                onSearchForValueChange = onSearchForValueChange,
                onOrderByValueChange = onOrderByValueChange,
                onSearchClick = onSearchClick
            )
        } else {
            CloudSearchPanelLandscape(
                searchFor = searchFor,
                orderBy = orderBy,
                theme = theme,
                fontSizeTextSp = fontSizeTextSp,
                onSearchForValueChange = onSearchForValueChange,
                onOrderByValueChange = onOrderByValueChange,
                onSearchClick = onSearchClick
            )
        }

        val listState = rememberLazyListState()

        when (searchState) {
            SearchState.EMPTY -> CommonSongListStub(fontSizeSongTitleSp, theme)
            SearchState.ERROR -> ErrorSongListStub(fontSizeSongTitleSp, theme)
            SearchState.LOADING -> CloudSearchProgress(theme)
            SearchState.LOADED, SearchState.LOADING_NEXT_PAGE -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    state = listState
                ) {
                    items(itemsAdapter.size) { index ->
                        val cloudSong = itemsAdapter.getItem(index)
                        cloudSong?.let {
                            CloudSongItem(
                                it, theme, fontSizeArtistSp, fontSizeSongTitleSp
                            ) {
                                onItemClick(index, it)
                            }
                        }
                    }
                }

                LaunchedEffect(needScroll) {
                    if (needScroll) {
                        if (scrollPosition < itemsAdapter.size) {
                            if (TestingConfig.isTesting) {
                                delay(100L)
                            }
                            listState.scrollToItem(scrollPosition)
                            delay(100L)
                            cloudViewModel.submitAction(UpdateNeedScroll(false))
                        }
                    } else {
                        snapshotFlow {
                            listState.firstVisibleItemIndex
                        }.collectLatest {
                            cloudViewModel.submitAction(UpdateScrollPosition(it))
                        }
                    }
                }
            }
        }

        when {
            searchState == SearchState.ERROR -> {}
            scrollPosition < itemsAdapter.size -> {
                cloudViewModel.submitAction(UpdateSearchState(SearchState.LOADED))
            }
            itemsAdapter.size > 0 -> {
                cloudViewModel.submitAction(UpdateSearchState(SearchState.LOADING_NEXT_PAGE))
                itemsAdapter.getItem(itemsAdapter.size - 1)
            }
            itemsAdapter.size == 0 && searchState != SearchState.EMPTY -> {
                cloudViewModel.submitAction(UpdateSearchState(SearchState.LOADING))
            }
        }
    }
}
