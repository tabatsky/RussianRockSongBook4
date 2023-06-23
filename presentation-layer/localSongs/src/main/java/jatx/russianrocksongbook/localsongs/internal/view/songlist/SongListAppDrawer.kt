package jatx.russianrocksongbook.localsongs.internal.view.songlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import jatx.russianrocksongbook.commonview.appbar.CommonSideAppBar
import jatx.russianrocksongbook.commonview.appbar.CommonTopAppBar
import jatx.russianrocksongbook.localsongs.R
import jatx.russianrocksongbook.localsongs.internal.viewmodel.LocalViewModel

@Composable
internal fun SongListAppDrawer(
    isPortrait: Boolean,
    onCloseDrawer: () -> Unit
) {
    val localViewModel = LocalViewModel.getInstance()

    val theme = localViewModel.settings.theme

    if (isPortrait) {
        Column(
            modifier = Modifier
                .background(theme.colorMain)
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
