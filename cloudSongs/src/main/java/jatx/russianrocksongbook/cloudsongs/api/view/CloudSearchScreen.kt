package jatx.russianrocksongbook.cloudsongs.api.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import jatx.russianrocksongbook.cloudsongs.R
import jatx.russianrocksongbook.cloudsongs.internal.paging.ItemsAdapter
import jatx.russianrocksongbook.cloudsongs.internal.viewmodel.CloudViewModel
import jatx.russianrocksongbook.cloudsongs.internal.viewmodel.SearchState
import jatx.russianrocksongbook.commonview.*
import jatx.russianrocksongbook.domain.models.CloudSong
import jatx.russianrocksongbook.domain.repository.OrderBy
import jatx.russianrocksongbook.domain.repository.ScalePow
import jatx.russianrocksongbook.domain.repository.Theme
import jatx.russianrocksongbook.testing.SEARCH_BUTTON
import jatx.russianrocksongbook.testing.SEARCH_PROGRESS
import jatx.russianrocksongbook.testing.TEXT_FIELD_SEARCH_FOR
import jatx.russianrocksongbook.testing.TestingConfig
import jatx.russianrocksongbook.viewmodel.CurrentScreenVariant
import kotlinx.coroutines.delay

@Composable
fun CloudSearchScreen() {
    val cloudViewModel: CloudViewModel = viewModel()
    val theme = cloudViewModel.settings.theme

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val W = this.maxWidth
        val H = this.maxHeight

        if (W < H) {
            val isPortrait = true
            val isLastOrientationPortrait by cloudViewModel
                .isLastOrientationPortrait.collectAsState()
            cloudViewModel.updateOrientationWasChanged(
                isPortrait != isLastOrientationPortrait
            )
            cloudViewModel.updateLastOrientationIsPortrait(true)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(theme.colorBg)
            ) {
                CommonTopAppBar(title = stringResource(id = R.string.title_activity_cloud_search))
                CloudSearchBody(
                    modifier = Modifier.weight(1.0f),
                    isPortrait = true
                )
            }
        } else {
            val isPortrait = false
            val isLastOrientationPortrait by cloudViewModel
                .isLastOrientationPortrait.collectAsState()
            cloudViewModel.updateOrientationWasChanged(
                isPortrait != isLastOrientationPortrait
            )
            cloudViewModel.updateLastOrientationIsPortrait(false)

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(theme.colorBg)
            ) {
                CommonSideAppBar(title = stringResource(id = R.string.title_activity_cloud_search))
                CloudSearchBody(
                    modifier = Modifier.weight(1.0f),
                    isPortrait = false
                )
            }
        }
    }
}

@Composable
private fun CloudSearchBody(
    modifier: Modifier,
    isPortrait: Boolean
) {
    val cloudViewModel: CloudViewModel = viewModel()
    val theme = cloudViewModel.settings.theme

    val scrollPosition by cloudViewModel.scrollPosition.collectAsState()
    val searchState by cloudViewModel.searchState.collectAsState()

    val cloudSongsFlow by cloudViewModel
        .cloudSongsFlow.collectAsState()

    val cloudSongItems = cloudSongsFlow.collectAsLazyPagingItems()
    val itemsAdapter = ItemsAdapter(
        cloudViewModel.snapshotHolder,
        cloudSongItems
    )

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
        cloudViewModel.selectCloudSong(index)
        cloudViewModel.selectScreen(CurrentScreenVariant.CLOUD_SONG_TEXT)
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
                val wasOrientationChanged by cloudViewModel.wasOrientationChanged.collectAsState()
                val needScroll by cloudViewModel.needScroll.collectAsState()

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    state = listState
                ) {
                    items(itemsAdapter.size) { index ->
                        val cloudSong = itemsAdapter.getItem(index)
                        cloudSong?.apply {
                            CloudSongItem(
                                this, theme, fontSizeArtistSp, fontSizeSongTitleSp
                            ) {
                                onItemClick(index, this)
                            }
                        }
                    }
                    if (!wasOrientationChanged && !needScroll) {
                        cloudViewModel.updateScrollPosition(listState.firstVisibleItemIndex)
                    }
                }
                if (wasOrientationChanged || needScroll) {
                    LaunchedEffect(Unit) {
                        if (scrollPosition < itemsAdapter.size) {
                            if (TestingConfig.isTesting) {
                                delay(100L)
                            }
                            listState.scrollToItem(scrollPosition)
                        }
                        cloudViewModel.updateNeedScroll(false)
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

@Composable
private fun CloudSearchPanelPortrait(
    searchFor: String,
    orderBy: OrderBy,
    theme: Theme,
    fontSizeTextSp: TextUnit,
    onSearchForValueChange: (String) -> Unit,
    onOrderByValueChange: (OrderBy) -> Unit,
    onSearchClick: () -> Unit
) {
    val size1 = dimensionResource(id = R.dimen.search_button_size) * 0.5f
    val size2 = dimensionResource(id = R.dimen.search_button_size) * 0.75f
    val size3 = dimensionResource(id = R.dimen.search_button_size) * 1.25f
    val padding = dimensionResource(id = R.dimen.empty)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(size3)
            .padding(all = padding)
    ) {
        Column(
            modifier = Modifier
                .weight(1.0f)
                .fillMaxHeight()
        ) {
            TextField(
                value = searchFor,
                modifier = Modifier
                    .testTag(TEXT_FIELD_SEARCH_FOR)
                    .fillMaxWidth()
                    .height(size2 - padding)
                    .padding(padding),
                colors = TextFieldDefaults
                    .textFieldColors(
                        backgroundColor = theme.colorMain,
                        textColor = theme.colorBg
                    ),
                textStyle = TextStyle(
                    fontSize = fontSizeTextSp
                ),
                onValueChange = onSearchForValueChange
            )
            Spinner(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(size1)
                    .padding(padding),
                theme = theme,
                fontSize = fontSizeTextSp,
                valueList = OrderBy.values().map { it.orderByRus }.toTypedArray(),
                initialPosition = orderBy.ordinal
            ) {
                onOrderByValueChange(OrderBy.values()[it])
            }
        }
        Box (
            modifier = Modifier
                .width(size3 - padding * 2)
                .height(size3 - padding * 2)
                .padding(padding)
        ) {
            OutlinedButton(
                modifier = Modifier
                    .testTag(SEARCH_BUTTON)
                    .fillMaxSize(),
                shape = RoundedCornerShape(size3 * 0.1f),
                contentPadding = PaddingValues(10.dp),
                colors = ButtonDefaults
                    .outlinedButtonColors(
                        backgroundColor = theme.colorCommon,
                        contentColor = theme.colorMain
                    ),
                onClick = onSearchClick,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_cloud_search),
                    contentDescription = ""
                )
            }
        }
    }
}

@Composable
private fun CloudSearchPanelLandscape(
    searchFor: String,
    orderBy: OrderBy,
    theme: Theme,
    fontSizeTextSp: TextUnit,
    onSearchForValueChange: (String) -> Unit,
    onOrderByValueChange: (OrderBy) -> Unit,
    onSearchClick: () -> Unit
) {
    val size = dimensionResource(id = R.dimen.search_button_size) * 0.75f
    val padding = dimensionResource(id = R.dimen.empty)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(size)
            .padding(start = padding, end = padding)
    ) {
        TextField(
            value = searchFor,
            modifier = Modifier
                .testTag(TEXT_FIELD_SEARCH_FOR)
                .weight(1.0f)
                .fillMaxHeight()
                .padding(padding),
            colors = TextFieldDefaults
                .textFieldColors(
                    backgroundColor = theme.colorMain,
                    textColor = theme.colorBg
                ),
            textStyle = TextStyle(
                fontSize = fontSizeTextSp
            ),
            onValueChange = onSearchForValueChange
        )
        Spinner(
            modifier = Modifier
                .weight(0.6f)
                .fillMaxHeight()
                .padding(padding),
            theme = theme,
            fontSize = fontSizeTextSp,
            valueList = OrderBy.values().map { it.orderByRus }.toTypedArray(),
            initialPosition = orderBy.ordinal
        ) {
            onOrderByValueChange(OrderBy.values()[it])
        }
        Box (
            modifier = Modifier
                .width(size)
                .height(size)
                .padding(padding)
        ) {
            OutlinedButton(
                modifier = Modifier
                    .testTag(SEARCH_BUTTON)
                    .fillMaxSize(),
                shape = RoundedCornerShape(size * 0.1f),
                contentPadding = PaddingValues(10.dp),
                colors = ButtonDefaults
                    .outlinedButtonColors(
                        backgroundColor = theme.colorCommon,
                        contentColor = theme.colorMain
                    ),
                onClick = onSearchClick,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_cloud_search),
                    contentDescription = ""
                )
            }
        }
    }
}


@Composable
private fun CloudSongItem(
    cloudSong: CloudSong,
    theme: Theme,
    fontSizeArtistSp: TextUnit,
    fontSizeSongTitleSp: TextUnit,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable {
                onClick()
            }
    ) {
        Text(
            text = cloudSong.visibleTitleWithRating,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = dimensionResource(id = R.dimen.padding_8),
                    bottom = dimensionResource(id = R.dimen.padding_8),
                    start = dimensionResource(id = R.dimen.padding_20)
                ),
            fontSize = fontSizeSongTitleSp,
            color = theme.colorMain
        )
        Text(
            text = cloudSong.artist,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = dimensionResource(id = R.dimen.padding_8),
                    bottom = dimensionResource(id = R.dimen.padding_8),
                    start = dimensionResource(id = R.dimen.padding_20)
                ),
            fontSize = fontSizeArtistSp,
            fontStyle = FontStyle.Italic,
            color = theme.colorMain
        )
        Divider(color = theme.colorCommon, thickness = 1.dp)
    }
}

@Composable
private fun CloudSearchProgress(
    theme: Theme
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .testTag(SEARCH_PROGRESS)
                .width(100.dp)
                .height(100.dp)
                .background(theme.colorBg),
            color = theme.colorMain
        )
    }
}