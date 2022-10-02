package jatx.russianrocksongbook.localsongs.internal.view.songlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import jatx.russianrocksongbook.commonview.appbar.CommonSideAppBar
import jatx.russianrocksongbook.commonview.appbar.CommonTopAppBar
import jatx.russianrocksongbook.localsongs.R
import jatx.russianrocksongbook.localsongs.internal.viewmodel.DrawerStateOpened
import jatx.russianrocksongbook.localsongs.internal.viewmodel.LocalViewModel

@Composable
internal fun SongListAppDrawer(
    onCloseDrawer: () -> Unit
) {
    val localViewModel: LocalViewModel = viewModel()

    val theme = localViewModel.settings.theme

    val drawerState by localViewModel.drawerState.collectAsState()
    val isActive = drawerState == DrawerStateOpened

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
                        SongListNavigationIcon(
                            onClick = onCloseDrawer,
                            isActive = isActive,
                            testTag = "drawerButtonMenu"
                        )
                    }
                )
                SongListMenuBody(
                    onCloseDrawer = onCloseDrawer
                )
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
                        SongListNavigationIcon(
                            onClick = onCloseDrawer,
                            isActive = isActive,
                            testTag = "drawerButtonMenu"
                        )
                    }
                )
                SongListMenuBody(
                    onCloseDrawer = onCloseDrawer
                )
            }
        }
    }
}
