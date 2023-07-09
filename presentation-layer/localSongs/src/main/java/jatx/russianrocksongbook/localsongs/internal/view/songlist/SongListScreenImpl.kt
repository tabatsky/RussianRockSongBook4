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
import jatx.russianrocksongbook.domain.repository.local.ARTIST_FAVORITE
import jatx.russianrocksongbook.localsongs.internal.viewmodel.LocalViewModel
import jatx.russianrocksongbook.localsongs.internal.viewmodel.VoiceCommandViewModel
import kotlinx.coroutines.launch

@Composable
internal fun SongListScreenImpl(
    artist: String,
    isBackFromSong: Boolean,
    passToSongWithTitle: String?
) {
    val localViewModel = LocalViewModel.getInstance()
    // for VoiceCommandViewModel initializing:
    VoiceCommandViewModel.getInstance()

    LaunchedEffect(Unit) {
        if (!isBackFromSong || artist == ARTIST_FAVORITE) {
            localViewModel.updateArtists()
        }
    }

    LaunchedEffect(Unit) {
        if (isBackFromSong) {
            localViewModel.updateNeedScroll(true)
        }
    }

    LaunchedEffect(artist to passToSongWithTitle) {
        localViewModel.showSongs(artist, passToSongWithTitle)
    }

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

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
                    }
                )
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
}

