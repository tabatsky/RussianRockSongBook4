package jatx.russianrocksongbook.donation.internal.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import jatx.russianrocksongbook.domain.repository.preferences.Theme
import jatx.russianrocksongbook.domain.repository.preferences.colorBlack
import jatx.russianrocksongbook.donation.api.view.donationLabel

@Composable
fun DonationItem(
    theme: Theme,
    index: Int,
    value: Int,
    onPurchaseClick: (Int, Boolean) -> Unit
) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        colors = ButtonDefaults
            .buttonColors(
                backgroundColor = theme.colorCommon,
                contentColor = colorBlack
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