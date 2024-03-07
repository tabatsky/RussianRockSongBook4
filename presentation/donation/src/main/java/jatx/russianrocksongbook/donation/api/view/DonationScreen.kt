package jatx.russianrocksongbook.donation.api.view

import androidx.compose.runtime.Composable
import jatx.russianrocksongbook.donation.internal.view.DonationScreenImpl

@Composable
fun DonationScreen() = DonationScreenImpl()

fun donationLabel(value: Int) = "Пожертвовать $value\$"