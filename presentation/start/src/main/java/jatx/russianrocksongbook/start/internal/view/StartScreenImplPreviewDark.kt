package jatx.russianrocksongbook.start.internal.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import jatx.russianrocksongbook.commonview.theme.DarkTheme

@Preview
@Composable
fun StartScreenImplPreviewDark() {
    val currentProgress = 12
    val totalProgress = 37

    val needShowStartScreen = true

    DarkTheme {
        StartScreenImplContent(
            currentProgress = currentProgress,
            totalProgress = totalProgress,
            needShowStartScreen = needShowStartScreen
        )
    }
}