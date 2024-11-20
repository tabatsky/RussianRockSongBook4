package jatx.russianrocksongbook.donation.internal.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import jatx.russianrocksongbook.commonview.theme.LightTheme

@Preview
@Composable
fun DonationScreenImplPreviewPortraitLight() {
    LightTheme {
        DonationScreenImplContent(
            submitAction = {}
        )
    }
}