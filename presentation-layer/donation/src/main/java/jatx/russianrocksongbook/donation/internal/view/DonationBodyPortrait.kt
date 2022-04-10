package jatx.russianrocksongbook.donation.internal.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import jatx.russianrocksongbook.domain.repository.preferences.Theme
import jatx.russianrocksongbook.donation.api.view.donationLabel
import jatx.russianrocksongbook.donationhelper.api.DONATIONS

@Composable
internal fun DonationBodyPortrait(
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
