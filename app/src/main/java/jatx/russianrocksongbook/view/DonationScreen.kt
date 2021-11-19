package jatx.russianrocksongbook.view

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import jatx.russianrocksongbook.R
import jatx.russianrocksongbook.helpers.DONATIONS
import jatx.russianrocksongbook.helpers.SKUS
import jatx.russianrocksongbook.preferences.Theme
import jatx.russianrocksongbook.viewmodel.MvvmViewModel
import jatx.sideappbar.SideAppBar

@Composable
fun DonationScreen(mvvmViewModel: MvvmViewModel) {
    val theme = mvvmViewModel.settings.theme

    val onPurchaseClick: (Int) -> Unit = { index ->
        mvvmViewModel.purchaseItem(SKUS[index])
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
                TopAppBar(
                    title = {
                        Text(text = stringResource(id = R.string.donation))
                    },
                    backgroundColor = theme.colorCommon,
                    navigationIcon = {
                        IconButton(onClick = {
                            mvvmViewModel.back { }
                        }) {
                            Icon(painterResource(id = R.drawable.ic_back), "")
                        }
                    }
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
                SideAppBar(
                    title = stringResource(id = R.string.donation),
                    backgroundColor = theme.colorCommon,
                    navigationIcon = {
                        CommonNavigationIcon(mvvmViewModel)
                    }
                )
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
    onPurchaseClick: (Int) -> Unit
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
                        onPurchaseClick(index)
                    }) {
                    Text(text = "Пожертвовать $value\$")
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
    onPurchaseClick: (Int) -> Unit
) {
    Column(Modifier.fillMaxSize().padding(end = 2.dp)) {
        Row(
            modifier = Modifier
                .weight(1.0f)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1.0f)
                    .padding(4.dp)
            ) {
                itemsIndexed(DONATIONS.take(4)) { index, value ->
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
                            onPurchaseClick(index)
                        }) {
                        Text(text = "Пожертвовать $value\$")
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
                itemsIndexed(DONATIONS.takeLast(4)) { index, value ->
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
                            onPurchaseClick(index + 4)
                        }) {
                        Text(text = "Пожертвовать $value\$")
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