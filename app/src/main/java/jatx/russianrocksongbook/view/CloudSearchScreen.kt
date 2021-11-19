package jatx.russianrocksongbook.view

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import jatx.sideappbar.SideAppBar
import kotlinx.coroutines.launch

@Composable
fun CloudSearchScreen(mvvmViewModel: MvvmViewModel) {
    var searchFor by remember { mutableStateOf("") }
    val onSearchForValueChange: (String) -> Unit = {
        searchFor = it
    }

    var orderBy by remember { mutableStateOf(OrderBy.BY_ID_DESC) }
    val onOrderByValueChange: (OrderBy) -> Unit = {
        orderBy = it
    }

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

    val onItemClick: (Int, CloudSong) -> Unit = { index, cloudSong ->
        println("selected: ${cloudSong.artist} - ${cloudSong.title}")
        mvvmViewModel.selectCloudSong(index)
        mvvmViewModel.selectScreen(CurrentScreenVariant.CLOUD_SONG_TEXT)
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val W = this.maxWidth
        val H = this.maxHeight

        if (W < H) {
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

                CloudSearchBody(
                    searchFor = searchFor,
                    theme = theme,
                    fontSizeTextSp = fontSizeTextSp,
                    fontSizeArtistSp = fontSizeArtistSp,
                    fontSizeSongTitleSp = fontSizeSongTitleSp,
                    isCloudLoading = isCloudLoading,
                    cloudSongList = cloudSongList,
                    position = position,
                    modifier = Modifier.weight(1.0f),
                    isPortrait = true,
                    onSearchForValueChange = onSearchForValueChange,
                    onOrderByValueChange = onOrderByValueChange,
                    onSearchClick = onSearchClick,
                    onItemClick = onItemClick
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(theme.colorBg)
            ) {
                SideAppBar(
                    title = stringResource(id = R.string.title_activity_cloud_search),
                    backgroundColor = theme.colorCommon,
                    navigationIcon = {
                        CommonNavigationIcon(mvvmViewModel)
                    }
                )


                CloudSearchBody(
                    searchFor = searchFor,
                    theme = theme,
                    fontSizeTextSp = fontSizeTextSp,
                    fontSizeArtistSp = fontSizeArtistSp,
                    fontSizeSongTitleSp = fontSizeSongTitleSp,
                    isCloudLoading = isCloudLoading,
                    cloudSongList = cloudSongList,
                    position = position,
                    modifier = Modifier.weight(1.0f),
                    isPortrait = false,
                    onSearchForValueChange = onSearchForValueChange,
                    onOrderByValueChange = onOrderByValueChange,
                    onSearchClick = onSearchClick,
                    onItemClick = onItemClick
                )
            }
        }
    }
}

@Composable
private fun CloudSearchBody(
    searchFor: String,
    theme: Theme,
    fontSizeTextSp: TextUnit,
    fontSizeArtistSp: TextUnit,
    fontSizeSongTitleSp: TextUnit,
    isCloudLoading: Boolean,
    cloudSongList: List<CloudSong>,
    position: Int,
    modifier: Modifier,
    isPortrait: Boolean,
    onSearchForValueChange: (String) -> Unit,
    onOrderByValueChange: (OrderBy) -> Unit,
    onSearchClick: () -> Unit,
    onItemClick: (Int, CloudSong) -> Unit
) {
    Column(
        modifier = modifier
    ) {
        if (isPortrait) {
            CloudSearchPanelPortrait(
                searchFor = searchFor,
                theme = theme,
                fontSizeTextSp = fontSizeTextSp,
                onSearchForValueChange = onSearchForValueChange,
                onOrderByValueChange = onOrderByValueChange,
                onSearchClick = onSearchClick
            )
        } else {
            CloudSearchPanelLandscape(
                searchFor = searchFor,
                theme = theme,
                fontSizeTextSp = fontSizeTextSp,
                onSearchForValueChange = onSearchForValueChange,
                onOrderByValueChange = onOrderByValueChange,
                onSearchClick = onSearchClick
            )
        }

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
                            onItemClick(index, cloudSong)
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
private fun CloudSearchPanelPortrait(
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
                initialPosition = 0
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
                    .fillMaxSize(),
                shape = RoundedCornerShape(size3 * 0.1f),
                contentPadding = PaddingValues(10.dp),
                colors = ButtonDefaults
                    .outlinedButtonColors(
                        backgroundColor = theme.colorCommon,
                        contentColor = Color.Black
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
            initialPosition = 0
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
                    .fillMaxSize(),
                shape = RoundedCornerShape(size * 0.1f),
                contentPadding = PaddingValues(10.dp),
                colors = ButtonDefaults
                    .outlinedButtonColors(
                        backgroundColor = theme.colorCommon,
                        contentColor = Color.Black
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
                .width(100.dp)
                .height(100.dp)
                .background(theme.colorBg),
            color = theme.colorMain
        )
    }
}