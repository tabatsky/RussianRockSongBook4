package jatx.russianrocksongbook.start.internal.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import jatx.russianrocksongbook.commonview.theme.LightTheme

@Preview
@Composable
fun StartScreenImplPreviewLight() {
    val currentProgress = 12
    val totalProgress = 37

    val needShowStartScreen = true

    LightTheme {
        StartScreenImplContent(
            currentProgress = currentProgress,
            totalProgress = totalProgress,
            needShowStartScreen = needShowStartScreen
        )
    }
}