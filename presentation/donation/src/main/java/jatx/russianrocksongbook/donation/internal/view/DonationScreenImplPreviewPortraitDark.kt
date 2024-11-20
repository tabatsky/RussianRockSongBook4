package jatx.russianrocksongbook.donation.internal.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import jatx.russianrocksongbook.commonview.theme.DarkTheme

@Preview
@Composable
fun DonationScreenImplPreviewPortraitDark() {
    DarkTheme {
        DonationScreenImplContent(
            submitAction = {}
        )
    }
}