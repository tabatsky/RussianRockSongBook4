package jatx.russianrocksongbook.localsongs.internal.view.songlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import jatx.russianrocksongbook.commonview.appbar.CommonSideAppBar
import jatx.russianrocksongbook.commonview.appbar.CommonTopAppBar
import jatx.russianrocksongbook.localsongs.R
import jatx.russianrocksongbook.localsongs.internal.viewmodel.LocalViewModel

@Composable
internal fun SongListAppDrawer(
    onCloseDrawer: () -> Unit
) {
    val localViewModel: LocalViewModel = viewModel()

    val theme = localViewModel.settings.theme

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
                val navigationFocusRequester = remember { FocusRequester() }
                CommonTopAppBar(
                    title = stringResource(R.string.menu),
                    navigationIcon = {
                        SongListNavigationIcon(
                            onClick = onCloseDrawer,
                            testTag = "drawerButtonMenu",
                            focusRequester = navigationFocusRequester
                        )
                    }
                )
                SongListMenuBody(
                    navigationFocusRequester = navigationFocusRequester,
                    onCloseDrawer = onCloseDrawer
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .background(theme.colorMain)
                    .fillMaxSize()
            ) {
                val navigationFocusRequester = remember { FocusRequester() }
                CommonSideAppBar(
                    title = stringResource(R.string.menu),
                    navigationIcon = {
                        SongListNavigationIcon(
                            onClick = onCloseDrawer,
                            testTag = "drawerButtonMenu",
                            focusRequester = navigationFocusRequester
                        )
                    }
                )
                SongListMenuBody(
                    navigationFocusRequester = navigationFocusRequester,
                    onCloseDrawer = onCloseDrawer
                )
            }
        }
    }
}
