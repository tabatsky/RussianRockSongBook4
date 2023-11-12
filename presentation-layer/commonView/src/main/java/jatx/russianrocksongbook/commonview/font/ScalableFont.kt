package jatx.russianrocksongbook.commonview.font

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import jatx.russianrocksongbook.domain.repository.preferences.FontScaler
import jatx.russianrocksongbook.domain.repository.preferences.ScalePow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun Dp.toScaledSp(fontScaler: StateFlow<FontScaler>, scalePow: ScalePow): TextUnit {
    val fontScale = fontScaler.collectAsState().value.getSpecificFontScale(scalePow)
    val fontSizeTextDp = this * fontScale
    val fontSizeTextSp = with(LocalDensity.current) {
        fontSizeTextDp.toSp()
    }
    return fontSizeTextSp
}