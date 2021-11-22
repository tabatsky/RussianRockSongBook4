package jatx.russianrocksongbook.localsongs.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import jatx.russianrocksongbook.commonview.*
import jatx.russianrocksongbook.localsongs.R
import jatx.russianrocksongbook.localsongs.viewmodel.LocalViewModel
import jatx.russianrocksongbook.model.data.*
import jatx.russianrocksongbook.model.domain.Song
import jatx.russianrocksongbook.model.preferences.ScalePow
import jatx.russianrocksongbook.model.preferences.Theme
import jatx.russianrocksongbook.viewmodel.CurrentScreenVariant
import kotlinx.coroutines.launch

private const val MAX_ARTIST_LENGTH_LANDSCAPE = 12
private const val MAX_ARTIST_LENGTH_PORTRAIT = 15

@Composable
fun SongListScreen() {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalDrawer(
        drawerState = drawerState,
        drawerContent = {
            SongListAppDrawer {
                scope.launch {
                    drawerState.close()
                }
            }
        },
        content = {
            SongListContent(
                openDrawer = {
                    scope.launch {
                        drawerState.open()
                    }
                }
            )
        }
    )
}

@Composable
private fun SongListContent(
    localViewModel: LocalViewModel = viewModel(),
    openDrawer: () -> Unit
) {
    val theme = localViewModel.settings.theme
    val artist by localViewModel.currentArtist.collectAsState()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val W = this.maxWidth
        val H = this.maxHeight

        if (W < H) {
            val visibleArtist = artist.crop(MAX_ARTIST_LENGTH_PORTRAIT)

            Column(
                modifier = Modifier
                    .background(theme.colorBg)
                    .fillMaxSize()
            ) {
                CommonTopAppBar(
                    title = visibleArtist,
                    navigationIcon = {
                        SongListNavigationIcon(onClick = openDrawer)
                    },
                    actions = {
                        SongListActions()
                    }
                )

                SongListBody()

                WhatsNewDialog()
            }
        } else {
            val visibleArtist = artist.crop(MAX_ARTIST_LENGTH_LANDSCAPE)

            Row(
                modifier = Modifier
                    .background(theme.colorBg)
                    .fillMaxSize()
            ) {
                CommonSideAppBar(
                    title = visibleArtist,
                    navigationIcon = {
                        SongListNavigationIcon(onClick = openDrawer)
                    },
                    actions = {
                        SongListActions()
                    }
                )

                SongListBody()

                WhatsNewDialog()
            }
        }
    }
}

@Composable
private fun SongListBody(
    localViewModel: LocalViewModel = viewModel()
) {
    val theme = localViewModel.settings.theme

    val songList by localViewModel.currentSongList.collectAsState()
    val position by localViewModel.currentSongPosition.collectAsState()

    val fontScale = localViewModel.settings.getSpecificFontScale(ScalePow.TEXT)
    val fontSizeDp = dimensionResource(id = R.dimen.text_size_20) * fontScale
    val fontSizeSp = with(LocalDensity.current) {
        fontSizeDp.toSp()
    }

    if (songList.isNotEmpty()) {
        val listState = rememberLazyListState()
        val coroutineScope = rememberCoroutineScope()
        LazyColumn(
            state = listState
        ) {
            itemsIndexed(songList) { index, song ->
                SongItem(song, theme, fontSizeSp) {
                    println("selected: ${song.artist} - ${song.title}")
                    localViewModel.selectSong(index)
                    localViewModel.selectScreen(CurrentScreenVariant.SONG_TEXT)
                }
            }
            coroutineScope.launch {
                listState.scrollToItem(position)
            }
        }
    } else {
        CommonSongListStub(fontSizeSp, theme)
    }
}

@Composable
private fun SongListAppDrawer(
    localViewModel: LocalViewModel = viewModel(),
    onCloseDrawer: () -> Unit
) {
    val theme = localViewModel.settings.theme

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val W = this.maxWidth
        val H = this.maxHeight

        if (W < H) {
            Column(
                modifier = Modifier
                    .background(theme.colorMain)
                    .fillMaxSize()
            ) {
                CommonTopAppBar(
                    title = stringResource(R.string.menu),
                    navigationIcon = {
                        SongListNavigationIcon(onClick = onCloseDrawer)
                    }
                )
                SongTextMenuBody(onCloseDrawer = onCloseDrawer)
            }
        } else {
            Row(
                modifier = Modifier
                    .background(theme.colorMain)
                    .fillMaxSize()
            ) {
                CommonSideAppBar(
                    title = stringResource(R.string.menu),
                    navigationIcon = {
                        SongListNavigationIcon(onClick = onCloseDrawer)
                    }
                )
                SongTextMenuBody(onCloseDrawer = onCloseDrawer)
            }
        }
    }
}

@Composable
private fun SongTextMenuBody(
    localViewModel: LocalViewModel = viewModel(),
    onCloseDrawer: () -> Unit
) {
    val theme = localViewModel.settings.theme
    val artistList by localViewModel.artistList.collectAsState()

    val fontScale = localViewModel.settings.getSpecificFontScale(ScalePow.MENU)
    val fontSizeDp = dimensionResource(id = R.dimen.text_size_20) * fontScale
    val fontSizeSp = with(LocalDensity.current) {
        fontSizeDp.toSp()
    }

    LazyColumn {
        items(artistList) { artist ->
            ArtistItem(artist, fontSizeSp, theme) {
                localViewModel.selectArtist(artist) {
                    localViewModel.selectSong(0)
                }
                onCloseDrawer()
            }
        }
    }
}

@Composable
private fun SongListActions(
    localViewModel: LocalViewModel = viewModel()
) {
    val theme = localViewModel.settings.theme
    var expanded by remember { mutableStateOf(false) }

    CommonIconButton(resId = R.drawable.ic_settings) {
        println("selected: settings")
        localViewModel.selectScreen(CurrentScreenVariant.SETTINGS)
    }
    CommonIconButton(resId = R.drawable.ic_question) {
        println("selected: question")
        expanded = !expanded
    }
    DropdownMenu(
        expanded = expanded,
        modifier = Modifier
            .background(theme.colorMain),
        onDismissRequest = {
            expanded = false
        }
    ) {
        DropdownMenuItem(onClick = {
            println("selected: review app")
            localViewModel.reviewApp()
        }) {
            Text(
                text = stringResource(id = R.string.item_review_app),
                color = theme.colorBg
            )
        }
        DropdownMenuItem(onClick = {
            println("selected: dev site")
            localViewModel.showDevSite()
        }) {
            Text(
                text = stringResource(id = R.string.item_dev_site),
                color = theme.colorBg
            )
        }
    }
}

@Composable
private fun SongListNavigationIcon(
    onClick: () -> Unit
) {
    CommonIconButton(
        resId = R.drawable.ic_drawer,
        onClick = onClick
    )
}

@Composable
private fun SongItem(
    song: Song,
    theme: Theme,
    fontSizeSp: TextUnit,
    onClick: () -> Unit
) {
    Text(
        text = song.title,
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(id = R.dimen.padding_20))
            .clickable {
                onClick()
            },
        fontSize = fontSizeSp,
        color = theme.colorMain
    )
    Divider(color = theme.colorCommon, thickness = 1.dp)
}

@Composable
private fun ArtistItem(
    artist: String,
    fontSizeSp: TextUnit,
    theme: Theme,
    onClick: () -> Unit
) {
    val isBold =
        (listOf(
            ARTIST_FAVORITE,
            ARTIST_ADD_ARTIST,
            ARTIST_ADD_SONG,
            ARTIST_CLOUD_SONGS,
            ARTIST_DONATION
        ).contains(artist))
    Text(
        text = artist,
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .clickable {
                onClick()
            },
        fontWeight = if (isBold) FontWeight.W700 else FontWeight.W400,
        fontSize = fontSizeSp,
        color = theme.colorBg
    )
    Divider(color = theme.colorCommon, thickness = 1.dp)
}
