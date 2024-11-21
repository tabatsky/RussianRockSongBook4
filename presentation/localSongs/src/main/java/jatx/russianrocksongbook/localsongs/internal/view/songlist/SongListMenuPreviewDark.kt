package jatx.russianrocksongbook.localsongs.internal.view.songlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.DrawerValue
import androidx.compose.material.ModalDrawer
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import jatx.russianrocksongbook.commonview.theme.DarkTheme
import jatx.russianrocksongbook.commonview.theme.LocalAppTheme

@Preview
@Composable
fun SongListMenuPreviewDark() {
    val drawerState = rememberDrawerState(DrawerValue.Open)
    val artistList = ('A'..'Z')
        .flatMap { letter ->
            (1..5).map { "$letter $it" }
        }
    val menuExpandedArtistGroup = "C"
    val menuScrollPosition = 0

    DarkTheme {
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
                        onCloseDrawer = {},
                        artistList = artistList,
                        menuExpandedArtistGroup = menuExpandedArtistGroup,
                        menuScrollPosition = menuScrollPosition,
                        submitAction = {}
                    )
                },
                content = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(LocalAppTheme.current.colorBg)
                    ) {}
                }
            )
        }
    }
}