package jatx.russianrocksongbook.donation.api.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import jatx.russianrocksongbook.commonview.appbar.CommonSideAppBar
import jatx.russianrocksongbook.commonview.appbar.CommonTopAppBar
import jatx.russianrocksongbook.domain.repository.preferences.Theme
import jatx.russianrocksongbook.donation.R
import jatx.russianrocksongbook.donation.internal.viewmodel.DonationViewModel
import jatx.russianrocksongbook.donationhelper.api.DONATIONS
import jatx.russianrocksongbook.donationhelper.api.DONATIONS_LANDSCAPE
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

@Composable
private fun DonationBodyPortrait(
    theme: Theme,
    onPurchaseClick: (Int, Boolean) -> Unit
) {
    Column {
        LazyColumn(
            modifier = Modifier
                .weight(1.0f)
                .padding(4.dp)
        ) {
            itemsIndexed(DONATIONS) { index, value ->
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults
                        .buttonColors(
                            backgroundColor = theme.colorCommon,
                            contentColor = theme.colorMain
                        ),
                    onClick = {
                        onPurchaseClick(index, false)
                    }) {
                    Text(text = donationLabel(value))
                }
                Divider(
                    color = theme.colorBg,
                    thickness = 4.dp
                )
            }
        }
    }
}

@Composable
private fun DonationBodyLandscape(
    theme: Theme,
    onPurchaseClick: (Int, Boolean) -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(end = 4.dp)) {
        Row(
            modifier = Modifier
                .weight(1.0f)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1.0f)
                    .padding(4.dp)
            ) {
                itemsIndexed(DONATIONS_LANDSCAPE.take(4)) { index, value ->
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults
                            .buttonColors(
                                backgroundColor = theme.colorCommon,
                                contentColor = theme.colorMain
                            ),
                        onClick = {
                            onPurchaseClick(index, true)
                        }) {
                        Text(text = donationLabel(value))
                    }
                    Divider(
                        color = theme.colorBg,
                        thickness = 4.dp
                    )
                }
            }
            LazyColumn(
                modifier = Modifier
                    .weight(1.0f)
                    .padding(4.dp)
            ) {
                itemsIndexed(DONATIONS_LANDSCAPE.takeLast(4)) { index, value ->
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults
                            .buttonColors(
                                backgroundColor = theme.colorCommon,
                                contentColor = theme.colorMain
                            ),
                        onClick = {
                            onPurchaseClick(index + 4, true)
                        }) {
                        Text(text = donationLabel(value))
                    }
                    Divider(
                        color = theme.colorBg,
                        thickness = 4.dp
                    )
                }
            }
        }
    }
}

fun donationLabel(value: Int) = "Пожертвовать $value\$"