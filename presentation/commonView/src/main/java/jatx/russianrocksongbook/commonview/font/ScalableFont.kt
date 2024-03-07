package jatx.russianrocksongbook.commonview.font

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import jatx.russianrocksongbook.commonview.theme.LocalFontScaler
import jatx.russianrocksongbook.domain.repository.preferences.ScalePow

@Composable
fun Dp.toScaledSp(scalePow: ScalePow): TextUnit {
    val fontScale = LocalFontScaler.current.getSpecificFontScale(scalePow)
    val fontSizeTextDp = this * fontScale
    val fontSizeTextSp = with(LocalDensity.current) {
        fontSizeTextDp.toSp()
    }
    return fontSizeTextSp
}