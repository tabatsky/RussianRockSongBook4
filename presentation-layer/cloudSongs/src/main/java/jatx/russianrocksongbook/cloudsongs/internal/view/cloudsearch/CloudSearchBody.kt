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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.paging.compose.collectAsLazyPagingItems
import jatx.russianrocksongbook.cloudsongs.R
import jatx.russianrocksongbook.cloudsongs.internal.paging.ItemsAdapter
import jatx.russianrocksongbook.cloudsongs.internal.viewmodel.CloudViewModel
import jatx.russianrocksongbook.cloudsongs.internal.viewmodel.SearchState
import jatx.russianrocksongbook.commonview.stub.CommonSongListStub
import jatx.russianrocksongbook.commonview.stub.ErrorSongListStub
import jatx.russianrocksongbook.domain.models.cloud.CloudSong
import jatx.russianrocksongbook.domain.repository.cloud.OrderBy
import jatx.russianrocksongbook.domain.repository.preferences.ScalePow
import jatx.russianrocksongbook.testing.TestingConfig
import jatx.russianrocksongbook.navigation.CurrentScreenVariant
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@Composable
internal fun CloudSearchBody(
    modifier: Modifier,
    isPortrait: Boolean
) {
    val cloudViewModel = CloudViewModel.getInstance()
    val theme = cloudViewModel.settings.theme

    val scrollPosition by cloudViewModel.scrollPosition.collectAsState()
    val searchState by cloudViewModel.searchState.collectAsState()

    val cloudSongsFlow by cloudViewModel
        .cloudSongsFlow.collectAsState()

    val cloudSongItems = cloudSongsFlow?.collectAsLazyPagingItems()
    val itemsAdapter = ItemsAdapter(cloudSongItems)

    val fontScale = cloudViewModel.settings.getSpecificFontScale(ScalePow.TEXT)
    val fontSizeTextDp = dimensionResource(id = R.dimen.text_size_16) * fontScale
    val fontSizeTextSp = with(LocalDensity.current) {
        fontSizeTextDp.toSp()
    }
    val fontSizeSongTitleDp = dimensionResource(id = R.dimen.text_size_20) * fontScale
    val fontSizeSongTitleSp = with(LocalDensity.current) {
        fontSizeSongTitleDp.toSp()
    }
    val fontSizeArtistDp = dimensionResource(id = R.dimen.text_size_24) * fontScale
    val fontSizeArtistSp = with(LocalDensity.current) {
        fontSizeArtistDp.toSp()
    }

    val searchFor by cloudViewModel.searchFor.collectAsState()
    val onSearchForValueChange: (String) -> Unit = {
        cloudViewModel.updateSearchFor(it)
    }

    val orderBy by cloudViewModel.orderBy.collectAsState()
    val onOrderByValueChange: (OrderBy) -> Unit = {
        if (orderBy != it) {
            cloudViewModel.updateOrderBy(it)
            cloudViewModel.cloudSearch(searchFor, it)
        }
    }

    val onSearchClick = {
        cloudViewModel.cloudSearch(searchFor, orderBy)
    }

    val onItemClick: (Int, CloudSong) -> Unit = { index, cloudSong ->
        println("selected: ${cloudSong.artist} - ${cloudSong.title}")
        cloudViewModel.selectScreen(CurrentScreenVariant.CLOUD_SONG_TEXT(index))
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
                val needScroll by cloudViewModel.needScroll.collectAsState()

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
                            cloudViewModel.updateNeedScroll(false)
                        }
                    } else {
                        snapshotFlow {
                            listState.firstVisibleItemIndex
                        }.collectLatest {
                            cloudViewModel.updateScrollPosition(it)
                        }
                    }
                }
            }
        }

        when {
            searchState == SearchState.ERROR -> {}
            scrollPosition < itemsAdapter.size -> {
                cloudViewModel.updateSearchState(SearchState.LOADED)
            }
            itemsAdapter.size > 0 -> {
                cloudViewModel.updateSearchState(SearchState.LOADING_NEXT_PAGE)
                itemsAdapter.getItem(itemsAdapter.size - 1)
            }
            itemsAdapter.size == 0 && searchState != SearchState.EMPTY -> {
                cloudViewModel.updateSearchState(SearchState.LOADING)
            }
        }
    }
}
