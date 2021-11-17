package jatx.russianrocksongbook.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import jatx.russianrocksongbook.R
import jatx.russianrocksongbook.data.OrderBy
import jatx.russianrocksongbook.domain.CloudSong
import jatx.russianrocksongbook.preferences.ScalePow
import jatx.russianrocksongbook.preferences.Theme
import jatx.russianrocksongbook.viewmodel.MvvmViewModel
import kotlinx.coroutines.launch

@Composable
fun CloudSearchScreen(mvvmViewModel: MvvmViewModel) {
    var searchFor by remember { mutableStateOf("") }
    var orderBy by remember { mutableStateOf(OrderBy.BY_ID_DESC) }

    val cloudSongList by mvvmViewModel.cloudSongList.collectAsState()
    val position by mvvmViewModel.cloudSongPosition.collectAsState()
    val isCloudLoading by mvvmViewModel.isCloudLoading.collectAsState()

    val theme = mvvmViewModel.settings.theme
    val fontScale = mvvmViewModel.settings.getSpecificFontScale(ScalePow.TEXT)
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

    val onSearchClick = {
        mvvmViewModel.cloudSearch(searchFor, orderBy)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.colorBg)
    ) {
        TopAppBar(
            title = {
                Text(text = stringResource(id = R.string.title_activity_cloud_search))
            },
            backgroundColor = theme.colorCommon,
            navigationIcon = {
                CommonNavigationIcon(mvvmViewModel)
            }
        )

        CloudSearchPanel(
            searchFor = searchFor,
            theme = theme,
            fontSizeTextSp = fontSizeTextSp,
            onSearchForValueChange = {
                searchFor = it
            },
            onOrderByValueChange = {
                orderBy = it
            },
            onSearchClick = onSearchClick
        )

        if (!isCloudLoading) {
            if (cloudSongList.isNotEmpty()) {
                val listState = rememberLazyListState()
                val coroutineScope = rememberCoroutineScope()
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    state = listState
                ) {
                    itemsIndexed(cloudSongList) { index, cloudSong ->
                        CloudSongItem(
                            cloudSong, theme, fontSizeArtistSp, fontSizeSongTitleSp
                        ) {
                            println("selected: ${cloudSong.artist} - ${cloudSong.title}")
                            mvvmViewModel.selectCloudSong(index)
                            mvvmViewModel.selectScreen(CurrentScreenVariant.CLOUD_SONG_TEXT)
                        }
                    }
                    coroutineScope.launch {
                        listState.scrollToItem(position)
                    }
                }
            } else {
                CommonSongListStub(fontSizeSongTitleSp, theme)
            }
        } else {
            CloudSearchProgress(theme)
        }
    }
}

@Composable
private fun CloudSearchPanel(
    searchFor: String,
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
    ) {
        Column(
            modifier = Modifier
                .weight(1.0f)
                .fillMaxHeight()
        ) {
            TextField(
                value = searchFor,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(size2)
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
            Divider(
                color = theme.colorBg,
                thickness = padding
            )
            Spinner(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(size1)
                    .padding(padding),
                theme = theme,
                fontSize = fontSizeTextSp,
                valueList = OrderBy.values().map { it.orderByRus }.toTypedArray(),
                initialPosition = 0
            ) {
                onOrderByValueChange(OrderBy.values()[it])
            }
        }
        Box (
            modifier = Modifier
                .width(size3)
                .height(size3)
                .padding(padding)
        ) {
            IconButton(
                modifier = Modifier
                    .background(theme.colorCommon)
                    .fillMaxSize(),
                onClick = onSearchClick
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_cloud_search),
                    contentDescription = "",
                    modifier = Modifier.padding(10.dp)
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
                .width(100.dp)
                .height(100.dp)
                .background(theme.colorBg),
            color = theme.colorMain
        )
    }
}