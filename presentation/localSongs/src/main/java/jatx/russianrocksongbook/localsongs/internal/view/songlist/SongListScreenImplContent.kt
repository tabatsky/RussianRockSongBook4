package jatx.russianrocksongbook.localsongs.internal.view.songlist

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.DrawerValue
import androidx.compose.material.ModalDrawer
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import jatx.russianrocksongbook.commonviewmodel.ShowSongs
import jatx.russianrocksongbook.commonviewmodel.UIAction
import jatx.russianrocksongbook.domain.models.local.Song
import jatx.russianrocksongbook.domain.repository.local.ARTIST_FAVORITE
import jatx.russianrocksongbook.localsongs.internal.viewmodel.UpdateArtists
import jatx.russianrocksongbook.localsongs.internal.viewmodel.UpdateSongListNeedScroll
import kotlinx.coroutines.launch

@Preview
@Composable
fun SongListScreenImplPreview() {
    val artistList = (1..30)
        .map { "Исполнитель $it" }
    val songList = (1..30)
        .map {
            Song(
                artist = "Исполнитель 1",
                title = "Название $it"
            )
        }

    SongListScreenImplContent(
        artist = "Исполнитель 1",
        isBackFromSomeScreen = false,
        songTitleToPass = null,
        artistList = artistList,
        currentArtist = "Исполнитель 1",
        songList = songList,
        songListScrollPosition = 0,
        songListNeedScroll = false,
        menuExpandedArtistGroup = "",
        menuScrollPosition = 0,
        voiceHelpDontAsk = false,
        submitAction = {}
    )
}

@Composable
internal fun SongListScreenImplContent(
    artist: String,
    isBackFromSomeScreen: Boolean,
    songTitleToPass: String?,
    artistList: List<String>,
    currentArtist: String,
    songList: List<Song>,
    songListScrollPosition: Int,
    songListNeedScroll: Boolean,
    menuExpandedArtistGroup: String,
    menuScrollPosition: Int,
    voiceHelpDontAsk: Boolean,
    submitAction: (UIAction) -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        if (!isBackFromSomeScreen || artist == ARTIST_FAVORITE) {
            submitAction(UpdateArtists)
        }
    }

    LaunchedEffect(Unit) {
        if (isBackFromSomeScreen) {
            submitAction(UpdateSongListNeedScroll(true))
        }
    }

    LaunchedEffect(Unit) {
        drawerState.snapTo(DrawerValue.Closed)
    }

    LaunchedEffect(artist to songTitleToPass) {
        submitAction(ShowSongs(artist, songTitleToPass))
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val W = this.maxWidth
        val H = this.maxHeight

        val isPortrait = W < H

        ModalDrawer(
            drawerState = drawerState,
            drawerContent = {
                SongListAppDrawer(
                    isPortrait = isPortrait,
                    onCloseDrawer = {
                        scope.launch {
                            drawerState.close()
                        }
                    },
                    artistList = artistList,
                    menuExpandedArtistGroup = menuExpandedArtistGroup,
                    menuScrollPosition = menuScrollPosition,
                    submitAction = submitAction
                )
            },
            content = {
                SongListContent(
                    openDrawer = {
                        scope.launch {
                            drawerState.open()
                        }
                    },
                    currentArtist = currentArtist,
                    songList = songList,
                    scrollPosition = songListScrollPosition,
                    needScroll = songListNeedScroll,
                    voiceHelpDontAsk = voiceHelpDontAsk,
                    submitAction = submitAction
                )
            }
        )
    }
}
