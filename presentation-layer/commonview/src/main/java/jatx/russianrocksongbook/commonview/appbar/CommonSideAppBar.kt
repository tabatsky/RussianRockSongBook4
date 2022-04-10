package jatx.russianrocksongbook.commonview.appbar

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import jatx.russianrocksongbook.commonview.buttons.CommonBackButton
import jatx.russianrocksongbook.domain.repository.preferences.colorDarkYellow
import jatx.sideappbar.SideAppBar

@Composable
fun CommonSideAppBar(
    title: String? = null,
    navigationIcon: @Composable () -> Unit = { CommonBackButton() },
    actions: @Composable ColumnScope.() -> Unit = {},
    appBarWidth: Dp = COMMON_APP_BAR_WIDTH
) {
    SideAppBar(
        title = title,
        backgroundColor = colorDarkYellow,
        navigationIcon = navigationIcon,
        actions = actions,
        appBarWidth = appBarWidth
    )
}
