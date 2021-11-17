package jatx.russianrocksongbook.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import jatx.russianrocksongbook.R
import jatx.russianrocksongbook.data.*
import jatx.russianrocksongbook.preferences.ScalePow
import jatx.russianrocksongbook.viewmodel.MvvmViewModel
import kotlinx.coroutines.launch

@Composable
fun SongListScreen(
    mvvmViewModel: MvvmViewModel
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(mvvmViewModel) {
                scope.launch {
                    drawerState.close()
                }
            }
        },
        content = {
            Content(
                mvvmViewModel = mvvmViewModel,
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
private fun Content(
    mvvmViewModel: MvvmViewModel,
    openDrawer: () -> Unit
) {
    val theme = mvvmViewModel.settings.theme
    val artist by mvvmViewModel.currentArtist.collectAsState()
    val songList by mvvmViewModel.currentSongList.collectAsState()
    val position by mvvmViewModel.currentSongPosition.collectAsState()

    val fontScale = mvvmViewModel.settings.getSpecificFontScale(ScalePow.TEXT)
    val fontSizeDp = dimensionResource(id = R.dimen.text_size_20) * fontScale
    val fontSizeSp = with(LocalDensity.current) {
        fontSizeDp.toSp()
    }

    Column(
        modifier = Modifier
            .background(theme.colorBg)
            .fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(text = artist)
            },
            backgroundColor = theme.colorCommon,
            navigationIcon = {
                IconButton(onClick = {
                    openDrawer()
                }) {
                    Icon(painterResource(id = R.drawable.ic_drawer), "")
                }
            },
            actions = {
                var expanded by remember { mutableStateOf(false) }
                IconButton(onClick = {
                    println("selected: settings")
                    mvvmViewModel.selectScreen(CurrentScreenVariant.SETTINGS)
                }) {
                    Icon(painterResource(id = R.drawable.ic_settings), "")
                }
                IconButton(onClick = {
                    println("selected: question")
                    expanded = !expanded
                }) {
                    Icon(painterResource(id = R.drawable.ic_question), "")
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
                        mvvmViewModel.onReviewApp()
                    }) {
                        Text(
                            text = stringResource(id = R.string.item_review_app),
                            color = theme.colorBg
                        )
                    }
                    DropdownMenuItem(onClick = {
                        println("selected: dev site")
                        mvvmViewModel.onShowDevSite()
                    }) {
                        Text(
                            text = stringResource(id = R.string.item_dev_site),
                            color = theme.colorBg
                        )
                    }
                }
            }
        )
        if (songList.isNotEmpty()) {
            val listState = rememberLazyListState()
            val coroutineScope = rememberCoroutineScope()
            LazyColumn(
                state = listState
            ) {
                itemsIndexed(songList) { index, song ->
                    Text(
                        text = song.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(dimensionResource(id = R.dimen.padding_20))
                            .clickable {
                                println("selected: ${song.artist} - ${song.title}")
                                mvvmViewModel.selectSong(index)
                                mvvmViewModel.selectScreen(CurrentScreenVariant.SONG_TEXT)
                            },
                        fontSize = fontSizeSp,
                        color = theme.colorMain
                    )
                    Divider(color = theme.colorCommon, thickness = 1.dp)
                }
                coroutineScope.launch {
                    listState.scrollToItem(position)
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.label_placeholder),
                    textAlign = TextAlign.Center,
                    fontSize = fontSizeSp,
                    color = theme.colorMain
                )
            }
        }
        WhatsNewDialog(
            mvvmViewModel = mvvmViewModel
        )
    }
}

@Composable
private fun AppDrawer(
    mvvmViewModel: MvvmViewModel,
    closeDrawer: () -> Unit
) {
    val theme = mvvmViewModel.settings.theme
    val artistList by mvvmViewModel.artistList.collectAsState()

    val fontScale = mvvmViewModel.settings.getSpecificFontScale(ScalePow.MENU)
    val fontSizeDp = dimensionResource(id = R.dimen.text_size_20) * fontScale
    val fontSizeSp = with(LocalDensity.current) {
        fontSizeDp.toSp()
    }

    Column(
        modifier = Modifier
            .background(theme.colorMain)
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        TopAppBar(
            title = {
                Text(text = "Меню")
            },
            backgroundColor = theme.colorCommon,
            navigationIcon = {
                IconButton(onClick = {
                    closeDrawer()
                }) {
                    Icon(painterResource(id = R.drawable.ic_drawer), "")
                }
            }
        )
        LazyColumn {
            items(artistList) { artist ->
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
                            mvvmViewModel.selectArtist(artist) {
                                mvvmViewModel.selectSong(0)
                            }
                            closeDrawer()
                        },
                    fontWeight = if (isBold) FontWeight.W700 else FontWeight.W400,
                    fontSize = fontSizeSp,
                    color = theme.colorBg
                )
                Divider(color = theme.colorCommon, thickness = 1.dp)
            }
        }
    }
}