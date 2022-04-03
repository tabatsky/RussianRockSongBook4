package jatx.russianrocksongbook.localsongs.api.view

import androidx.compose.material.DrawerValue
import androidx.compose.material.ModalDrawer
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import jatx.russianrocksongbook.localsongs.internal.view.songlist.SongListAppDrawer
import jatx.russianrocksongbook.localsongs.internal.view.songlist.SongListContent
import kotlinx.coroutines.launch

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









