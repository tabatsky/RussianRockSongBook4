package jatx.russianrocksongbook.whatsnewdialog.internal.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import jatx.russianrocksongbook.commonview.theme.LightTheme
import jatx.russianrocksongbook.commonview.theme.LocalAppTheme

@Preview
@Composable
fun WhatsNewDialogImplPreviewLight() {
    LightTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(LocalAppTheme.current.colorBg)
        ) {
            WhatsNewDialogImpl(
                appWasUpdatedWhenNoViewModel = true
            )
        }
    }
}