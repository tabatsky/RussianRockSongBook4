package jatx.russianrocksongbook.commonview.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import jatx.russianrocksongbook.commonviewmodel.CommonViewModel
import jatx.russianrocksongbook.domain.repository.preferences.Theme

val LocalAppTheme = compositionLocalOf {
    Theme.DARK
}

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val theme by CommonViewModel.getInstance().theme.collectAsState()
    CompositionLocalProvider(
        LocalAppTheme provides theme
    ) {
        content()
    }
}