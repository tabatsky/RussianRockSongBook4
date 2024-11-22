package jatx.russianrocksongbook.commonview.appbar

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import jatx.russianrocksongbook.commonview.buttons.CommonBackButton
import jatx.russianrocksongbook.domain.repository.preferences.colorDarkYellow

@Composable
fun CommonTopAppBar(
    title: String? = null,
    titleTestTag: String? = null,
    navigationIcon: @Composable () -> Unit = { CommonBackButton() },
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = {
            title?.let {
                Text(
                    text = it,
                    fontWeight = FontWeight.W700,
                    softWrap = false,
                    modifier = titleTestTag?.let { testTag ->
                        Modifier.testTag(testTag)
                    } ?: Modifier
                )
            }
        },
        backgroundColor = colorDarkYellow,
        navigationIcon = navigationIcon,
        actions = actions
    )
}
