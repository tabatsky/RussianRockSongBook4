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
import jatx.russianrocksongbook.commonviewmodel.ShowSongs
import jatx.russianrocksongbook.domain.repository.local.ARTIST_FAVORITE
import jatx.russianrocksongbook.localsongs.internal.viewmodel.LocalViewModel
import jatx.russianrocksongbook.localsongs.internal.viewmodel.UpdateArtists
import jatx.russianrocksongbook.localsongs.internal.viewmodel.UpdateSongListNeedScroll
import jatx.russianrocksongbook.localsongs.internal.viewmodel.VoiceCommandViewModel
import kotlinx.coroutines.launch

@Composable
internal fun SongListScreenImpl(
    artist: String,
    isBackFromSomeScreen: Boolean,
    passToSongWithTitle: String?
) {
    val localViewModel = LocalViewModel.getInstance()
    InitVoiceCommandViewModel()

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        if (!isBackFromSomeScreen || artist == ARTIST_FAVORITE) {
            localViewModel.submitAction(UpdateArtists)
        }
    }

    LaunchedEffect(Unit) {
        if (isBackFromSomeScreen) {
            localViewModel.submitAction(UpdateSongListNeedScroll(true))
            scope.launch {
                drawerState.close()
            }
        }
    }

    LaunchedEffect(artist to passToSongWithTitle) {
        localViewModel.submitAction(ShowSongs(artist, passToSongWithTitle))
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

@Composable
private fun InitVoiceCommandViewModel() {
    VoiceCommandViewModel.getInstance()
}