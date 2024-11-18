package jatx.russianrocksongbook.donation.internal.view

import androidx.compose.runtime.Composable
import jatx.russianrocksongbook.donation.internal.viewmodel.DonationViewModel

@Composable
internal fun DonationScreenImpl() {
    val donationViewModel = DonationViewModel.getInstance()
    val submitAction = donationViewModel::submitAction

    DonationScreenImplContent(
        submitAction = submitAction
    )
}