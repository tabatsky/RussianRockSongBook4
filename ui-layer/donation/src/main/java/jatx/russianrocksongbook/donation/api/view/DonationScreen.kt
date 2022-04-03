package jatx.russianrocksongbook.donation.api.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import jatx.russianrocksongbook.commonview.appbar.CommonSideAppBar
import jatx.russianrocksongbook.commonview.appbar.CommonTopAppBar
import jatx.russianrocksongbook.donation.R
import jatx.russianrocksongbook.donation.internal.view.DonationBodyLandscape
import jatx.russianrocksongbook.donation.internal.view.DonationBodyPortrait
import jatx.russianrocksongbook.donation.internal.viewmodel.DonationViewModel
import jatx.russianrocksongbook.donationhelper.api.SKUS
import jatx.russianrocksongbook.donationhelper.api.SKUS_LANDSCAPE

@Composable
fun DonationScreen() {
    val donationViewModel: DonationViewModel = viewModel()

    val theme = donationViewModel.settings.theme

    val onPurchaseClick: (Int, Boolean) -> Unit = { index, isLandscape ->
        val sku = if (!isLandscape) SKUS[index] else SKUS_LANDSCAPE[index]
        donationViewModel.purchaseItem(sku)
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val W = this.maxWidth
        val H = this.minHeight

        if (W < H) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = theme.colorBg)
            ) {
                CommonTopAppBar(title = stringResource(id = R.string.title_donation))
                DonationBodyPortrait(
                    theme = theme,
                    onPurchaseClick = onPurchaseClick
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = theme.colorBg)
            ) {
                CommonSideAppBar(title = stringResource(id = R.string.title_donation))
                DonationBodyLandscape(
                    theme = theme,
                    onPurchaseClick = onPurchaseClick
                )
            }
        }
    }
}

fun donationLabel(value: Int) = "Пожертвовать $value\$"