package jatx.russianrocksongbook.localsongs.internal.view.songlist

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import jatx.dpad.dpadFocusable
import jatx.russianrocksongbook.commonview.buttons.CommonIconButton
import jatx.russianrocksongbook.localsongs.R
import jatx.russianrocksongbook.testing.DRAWER_BUTTON_MAIN

@Composable
internal fun SongListNavigationIcon(
    onClick: () -> Unit,
    isActive: Boolean,
    testTag: String? = null
) {
    val modifier = if (isActive)
        Modifier.dpadFocusable(onClick = onClick)
    else
        Modifier.focusProperties {
            canFocus = false
        }

    CommonIconButton(
        resId = R.drawable.ic_drawer,
        testTag = testTag ?: DRAWER_BUTTON_MAIN,
        modifier = modifier,
        onClick = onClick
    )
}