package jatx.russianrocksongbook.localsongs.internal.view.songlist

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import jatx.russianrocksongbook.commonview.buttons.CommonIconButton
import jatx.russianrocksongbook.localsongs.R
import jatx.russianrocksongbook.testing.DRAWER_BUTTON_MAIN

@Composable
internal fun SongListNavigationIcon(
    onClick: () -> Unit,
    focusRequester: FocusRequester,
    testTag: String? = null
) {
    val modifier = Modifier
        .focusRequester(focusRequester)
    CommonIconButton(
        resId = R.drawable.ic_drawer,
        testTag = testTag ?: DRAWER_BUTTON_MAIN,
        modifier = modifier,
        onClick = onClick
    )
}