package jatx.russianrocksongbook.localsongs.internal.view.songlist

import androidx.compose.material.DrawerValue
import androidx.compose.material.ModalDrawer
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import jatx.russianrocksongbook.domain.repository.local.ARTIST_FAVORITE
import jatx.russianrocksongbook.localsongs.internal.viewmodel.LocalViewModel
import kotlinx.coroutines.launch

@Composable
internal fun SongListScreenImpl(
    artist: String,
    isBackFromSong: Boolean,
    passToSongWithTitle: String?
) {
    val localViewModel: LocalViewModel = viewModel()

    LaunchedEffect(Unit) {
        if (!isBackFromSong || artist == ARTIST_FAVORITE) {
            localViewModel.updateArtists()
        }
    }

    LaunchedEffect(artist to passToSongWithTitle) {
        localViewModel.showSongs(artist, passToSongWithTitle)
    }

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalDrawer(
        drawerState = drawerState,
        drawerContent = {
            SongListAppDrawer(
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
