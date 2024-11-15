package jatx.russianrocksongbook.cloudsongs.internal.view.cloudsearch

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.paging.compose.LazyPagingItems
import jatx.russianrocksongbook.cloudsongs.R
import jatx.russianrocksongbook.cloudsongs.internal.paging.ItemsAdapter
import jatx.russianrocksongbook.cloudsongs.internal.viewmodel.PerformCloudSearch
import jatx.russianrocksongbook.cloudsongs.internal.viewmodel.SearchState
import jatx.russianrocksongbook.cloudsongs.internal.viewmodel.UpdateCloudSongListNeedScroll
import jatx.russianrocksongbook.cloudsongs.internal.viewmodel.UpdateOrderBy
import jatx.russianrocksongbook.cloudsongs.internal.viewmodel.UpdateCloudSongListScrollPosition
import jatx.russianrocksongbook.cloudsongs.internal.viewmodel.UpdateSearchFor
import jatx.russianrocksongbook.cloudsongs.internal.viewmodel.UpdateSearchState
import jatx.russianrocksongbook.commonview.font.toScaledSp
import jatx.russianrocksongbook.commonview.stub.CommonSongListStub
import jatx.russianrocksongbook.commonview.stub.ErrorSongListStub
import jatx.russianrocksongbook.commonview.theme.LocalAppTheme
import jatx.russianrocksongbook.commonviewmodel.SelectScreen
import jatx.russianrocksongbook.commonviewmodel.UIAction
import jatx.russianrocksongbook.domain.models.cloud.CloudSong
import jatx.russianrocksongbook.domain.repository.cloud.CloudSearchOrderBy
import jatx.russianrocksongbook.domain.repository.preferences.ScalePow
import jatx.russianrocksongbook.testing.TestingConfig
import jatx.russianrocksongbook.navigation.ScreenVariant
import jatx.spinner.SpinnerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@Composable
internal fun CloudSearchBody(
    modifier: Modifier,
    isPortrait: Boolean,
    searchState: SearchState,
    needScroll: Boolean,
    scrollPosition: Int,
    cloudSongItems: LazyPagingItems<CloudSong>?,
    searchFor: String,
    orderBy: CloudSearchOrderBy,
    spinnerStateOrderBy: MutableState<SpinnerState>,
    submitAction: (UIAction) -> Unit
) {
    val theme = LocalAppTheme.current

    val itemsAdapter = ItemsAdapter(cloudSongItems)
    Log.e("items", itemsAdapter.size.toString())

    val fontSizeTextSp = dimensionResource(id = R.dimen.text_size_16)
        .toScaledSp(ScalePow.TEXT)
    val fontSizeSongTitleSp = dimensionResource(id = R.dimen.text_size_20)
        .toScaledSp(ScalePow.TEXT)
    val fontSizeArtistSp = dimensionResource(id = R.dimen.text_size_24)
        .toScaledSp(ScalePow.TEXT)

    val onSearchForValueChange: (String) -> Unit = {
        submitAction(UpdateSearchFor(it))
    }

    val onOrderByValueChange: (CloudSearchOrderBy) -> Unit = {
        if (orderBy != it) {
            submitAction(UpdateOrderBy(it))
            submitAction(PerformCloudSearch(searchFor, it))
        }
    }

    val onSearchClick = {
        submitAction(PerformCloudSearch(searchFor, orderBy))
    }

    val onItemClick: (Int, CloudSong) -> Unit = { index, cloudSong ->
        println("selected: ${cloudSong.artist} - ${cloudSong.title}")
        submitAction(SelectScreen(ScreenVariant.CloudSongText(index)))
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
                onSearchClick = onSearchClick,
                spinnerStateOrderBy = spinnerStateOrderBy
            )
        } else {
            CloudSearchPanelLandscape(
                searchFor = searchFor,
                orderBy = orderBy,
                theme = theme,
                fontSizeTextSp = fontSizeTextSp,
                onSearchForValueChange = onSearchForValueChange,
                onOrderByValueChange = onOrderByValueChange,
                onSearchClick = onSearchClick,
                spinnerStateOrderBy = spinnerStateOrderBy
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
                            submitAction(UpdateCloudSongListNeedScroll(false))
                        }
                    } else {
                        snapshotFlow {
                            listState.firstVisibleItemIndex
                        }.collectLatest {
                            submitAction(UpdateCloudSongListScrollPosition(it))
                        }
                    }
                }
            }
        }

        when {
            searchState == SearchState.ERROR -> {}
            scrollPosition < itemsAdapter.size -> {
                submitAction(UpdateSearchState(SearchState.LOADED))
            }
            itemsAdapter.size > 0 -> {
                submitAction(UpdateSearchState(SearchState.LOADING_NEXT_PAGE))
                itemsAdapter.getItem(itemsAdapter.size - 1)
            }
            itemsAdapter.size == 0 && searchState != SearchState.EMPTY -> {
                submitAction(UpdateSearchState(SearchState.LOADING))
            }
        }
    }
}
