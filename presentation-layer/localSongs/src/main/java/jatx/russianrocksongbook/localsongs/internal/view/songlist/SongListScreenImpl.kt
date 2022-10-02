package jatx.russianrocksongbook.localsongs.internal.view.songlist

import androidx.compose.material.DrawerValue
import androidx.compose.material.ModalDrawer
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import jatx.russianrocksongbook.localsongs.internal.viewmodel.DrawerStateClosed
import jatx.russianrocksongbook.localsongs.internal.viewmodel.DrawerStateOpened
import jatx.russianrocksongbook.localsongs.internal.viewmodel.LocalViewModel
import kotlinx.coroutines.launch

@Composable
internal fun SongListScreenImpl() {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val localViewModel: LocalViewModel = viewModel()

    ModalDrawer(
        drawerState = drawerState,
        drawerContent = {
            SongListAppDrawer(
                onCloseDrawer = {
                    scope.launch {
                        drawerState.close()
                        localViewModel.updateDrawerState(DrawerStateClosed)
                    }
                }
            )
        },
        content = {
            SongListContent(
                openDrawer = {
                    scope.launch {
                        drawerState.open()
                        localViewModel.updateDrawerState(DrawerStateOpened)
                    }
                }
            )
        }
    )
}
