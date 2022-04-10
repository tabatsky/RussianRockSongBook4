package jatx.russianrocksongbook.localsongs.internal.view.songlist

import androidx.compose.runtime.Composable
import jatx.russianrocksongbook.commonview.buttons.CommonIconButton
import jatx.russianrocksongbook.localsongs.R
import jatx.russianrocksongbook.testing.DRAWER_BUTTON_MAIN

@Composable
internal fun SongListNavigationIcon(
    onClick: () -> Unit,
    testTag: String? = null
) {
    CommonIconButton(
        resId = R.drawable.ic_drawer,
        testTag = testTag ?: DRAWER_BUTTON_MAIN,
        onClick = onClick
    )
}