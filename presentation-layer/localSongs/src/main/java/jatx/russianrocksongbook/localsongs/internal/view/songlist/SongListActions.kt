package jatx.russianrocksongbook.localsongs.internal.view.songlist

import androidx.compose.foundation.background
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import jatx.dpad.dpadFocusable
import jatx.russianrocksongbook.commonview.buttons.CommonIconButton
import jatx.russianrocksongbook.localsongs.R
import jatx.russianrocksongbook.localsongs.internal.viewmodel.LocalViewModel
import jatx.russianrocksongbook.testing.SETTINGS_BUTTON
import jatx.russianrocksongbook.viewmodel.CurrentScreenVariant

@Composable
internal fun SongListActions() {
    val localViewModel: LocalViewModel = viewModel()

    val theme = localViewModel.settings.theme
    var expanded by remember { mutableStateOf(false) }

    val onSettingsClick = {
        println("selected: settings")
        localViewModel.selectScreen(CurrentScreenVariant.SETTINGS)
    }
    val settingsModifier = Modifier.dpadFocusable(onSettingsClick)
    CommonIconButton(
        testTag = SETTINGS_BUTTON,
        resId = R.drawable.ic_settings,
        modifier = settingsModifier,
        onClick = onSettingsClick
    )

    val onQuestionClick = {
        println("selected: question")
        expanded = !expanded
    }
    val questionModifier = Modifier.dpadFocusable(onQuestionClick)
    CommonIconButton(
        resId = R.drawable.ic_question,
        modifier = questionModifier,
        onClick = onQuestionClick
    )
    DropdownMenu(
        expanded = expanded,
        modifier = Modifier
            .background(theme.colorMain),
        onDismissRequest = {
            expanded = false
        }
    ) {
        DropdownMenuItem(onClick = {
            println("selected: review app")
            localViewModel.reviewApp()
        }) {
            Text(
                text = stringResource(id = R.string.item_review_app),
                color = theme.colorBg
            )
        }
        DropdownMenuItem(onClick = {
            println("selected: dev site")
            localViewModel.showDevSite()
        }) {
            Text(
                text = stringResource(id = R.string.item_dev_site),
                color = theme.colorBg
            )
        }
    }
}
