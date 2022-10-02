package jatx.russianrocksongbook.localsongs.internal.view.songlist

import androidx.compose.material.DrawerValue
import androidx.compose.material.ModalDrawer
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.*
import kotlinx.coroutines.launch

@Composable
internal fun SongListScreenImpl() {
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
                },
                isActive = drawerState.isOpen
            )
        },
        content = {
            SongListContent(
                openDrawer = {
                    scope.launch {
                        drawerState.open()
                    }
                },
                isActive = drawerState.isClosed
            )
        }
    )
}
