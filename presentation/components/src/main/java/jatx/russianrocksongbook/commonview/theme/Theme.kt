package jatx.russianrocksongbook.commonview.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import jatx.russianrocksongbook.commonviewmodel.CommonViewModel
import jatx.russianrocksongbook.domain.repository.preferences.FontScale
import jatx.russianrocksongbook.domain.repository.preferences.FontScaler
import jatx.russianrocksongbook.domain.repository.preferences.Theme
import jatx.russianrocksongbook.preferences.repository.FontScalerImpl

val LocalAppTheme = compositionLocalOf {
    Theme.DARK
}

val LocalFontScaler = compositionLocalOf<FontScaler> {
    FontScalerImpl(FontScale.M.scale)
}

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val theme by CommonViewModel.getInstance().theme.collectAsState()
    val fontScaler by CommonViewModel.getInstance().fontScaler.collectAsState()
    CompositionLocalProvider(
        LocalAppTheme provides theme,
        LocalFontScaler provides fontScaler
    ) {
        content()
    }
}

@Composable
fun DarkTheme(content: @Composable () -> Unit) {
    val theme = Theme.DARK
    val fontScaler = FontScalerImpl(FontScale.M.scale)
    CompositionLocalProvider(
        LocalAppTheme provides theme,
        LocalFontScaler provides fontScaler
    ) {
        content()
    }
}

@Composable
fun LightTheme(content: @Composable () -> Unit) {
    val theme = Theme.LIGHT
    val fontScaler = FontScalerImpl(FontScale.M.scale)
    CompositionLocalProvider(
        LocalAppTheme provides theme,
        LocalFontScaler provides fontScaler
    ) {
        content()
    }
}