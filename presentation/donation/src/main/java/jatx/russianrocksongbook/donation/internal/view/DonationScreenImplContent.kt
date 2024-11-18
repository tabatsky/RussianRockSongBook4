package jatx.russianrocksongbook.donation.internal.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import jatx.russianrocksongbook.commonview.appbar.CommonSideAppBar
import jatx.russianrocksongbook.commonview.appbar.CommonTopAppBar
import jatx.russianrocksongbook.commonview.theme.LocalAppTheme
import jatx.russianrocksongbook.commonviewmodel.UIAction
import jatx.russianrocksongbook.donation.R
import jatx.russianrocksongbook.donation.internal.viewmodel.PurchaseItem
import jatx.russianrocksongbook.donationhelper.api.SKUS
import jatx.russianrocksongbook.donationhelper.api.SKUS_LANDSCAPE
import jatx.russianrocksongbook.testing.APP_BAR_TITLE

@Composable
fun DonationScreenImplContent(
    submitAction: (UIAction) -> Unit
) {
    val theme = LocalAppTheme.current

    val onPurchaseClick: (Int, Boolean) -> Unit = { index, isLandscape ->
        val sku = if (!isLandscape) SKUS[index] else SKUS_LANDSCAPE[index]
        submitAction(PurchaseItem(sku))
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
                CommonTopAppBar(
                    title = stringResource(id = R.string.title_donation),
                    titleTestTag = APP_BAR_TITLE
                )
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
                CommonSideAppBar(
                    title = stringResource(id = R.string.title_donation),
                    titleTestTag = APP_BAR_TITLE
                )
                DonationBodyLandscape(
                    theme = theme,
                    onPurchaseClick = onPurchaseClick
                )
            }
        }
    }
}