package jatx.russianrocksongbook.donation.internal.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import jatx.russianrocksongbook.commonview.theme.LightTheme

@Preview(widthDp = 640, heightDp = 360)
@Composable
fun DonationScreenImplPreviewLandscapeLight() {
    LightTheme {
        DonationScreenImplContent(
            submitAction = {}
        )
    }
}