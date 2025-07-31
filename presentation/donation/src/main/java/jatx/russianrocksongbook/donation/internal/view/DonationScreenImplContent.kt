package jatx.russianrocksongbook.donation.internal.view

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import jatx.russianrocksongbook.commonview.appbar.CommonSideAppBar
import jatx.russianrocksongbook.commonview.appbar.CommonTopAppBar
import jatx.russianrocksongbook.commonview.theme.LocalAppTheme
import jatx.russianrocksongbook.commonviewmodel.UIAction
import jatx.russianrocksongbook.donation.R
import jatx.russianrocksongbook.donation.internal.viewmodel.PurchaseItem
import jatx.russianrocksongbook.donationhelper.api.SKUS
import jatx.russianrocksongbook.donationhelper.api.SKUS_LANDSCAPE
import jatx.russianrocksongbook.testing.APP_BAR_TITLE

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun DonationScreenImplContent(
    submitAction: (UIAction) -> Unit
) {
    val theme = LocalAppTheme.current

    val onPurchaseClick: (Int, Boolean) -> Unit = { index, isLandscape ->
        val sku = if (!isLandscape) SKUS[index] else SKUS_LANDSCAPE[index]
        submitAction(PurchaseItem(sku))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val configuration = LocalConfiguration.current
        val W = configuration.screenWidthDp.dp
        val H = configuration.screenHeightDp.dp

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