package jatx.russianrocksongbook.localsongs.internal.view.songlist

import androidx.compose.foundation.background
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import jatx.russianrocksongbook.commonview.buttons.CommonIconButton
import jatx.russianrocksongbook.commonview.theme.LocalAppTheme
import jatx.russianrocksongbook.commonviewmodel.SelectScreen
import jatx.russianrocksongbook.commonviewmodel.UIAction
import jatx.russianrocksongbook.localsongs.R
import jatx.russianrocksongbook.localsongs.internal.viewmodel.ReviewApp
import jatx.russianrocksongbook.localsongs.internal.viewmodel.ShowDevSite
import jatx.russianrocksongbook.testing.SETTINGS_BUTTON
import jatx.russianrocksongbook.navigation.*

@Composable
internal fun SongListActions(
    submitAction: (UIAction) -> Unit
) {
    val theme = LocalAppTheme.current
    var expanded by remember { mutableStateOf(false) }

    val onSettingsClick = {
        println("selected: settings")
        submitAction(SelectScreen(SettingsScreenVariant))
    }
    CommonIconButton(
        testTag = SETTINGS_BUTTON,
        resId = R.drawable.ic_settings,
        onClick = onSettingsClick
    )

    val onQuestionClick = {
        println("selected: question")
        expanded = !expanded
    }
    CommonIconButton(
        resId = R.drawable.ic_question,
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
            submitAction(ReviewApp)
        }) {
            Text(
                text = stringResource(id = R.string.item_review_app),
                color = theme.colorBg
            )
        }
        DropdownMenuItem(onClick = {
            println("selected: dev site")
            submitAction(ShowDevSite)
        }) {
            Text(
                text = stringResource(id = R.string.item_dev_site),
                color = theme.colorBg
            )
        }
    }
}
