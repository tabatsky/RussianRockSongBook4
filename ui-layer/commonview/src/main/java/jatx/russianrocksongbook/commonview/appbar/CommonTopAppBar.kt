package jatx.russianrocksongbook.commonview.appbar

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import jatx.russianrocksongbook.commonview.buttons.CommonBackButton
import jatx.russianrocksongbook.domain.repository.preferences.colorDarkYellow

@Composable
fun CommonTopAppBar(
    title: String? = null,
    navigationIcon: @Composable () -> Unit = { CommonBackButton() },
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = {
            title?.let {
                Text(
                    text = it,
                    softWrap = false
                )
            }
        },
        backgroundColor = colorDarkYellow,
        navigationIcon = navigationIcon,
        actions = actions
    )
}
