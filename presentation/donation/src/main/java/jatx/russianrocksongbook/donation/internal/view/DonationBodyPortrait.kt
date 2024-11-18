package jatx.russianrocksongbook.donation.internal.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import jatx.russianrocksongbook.domain.repository.preferences.Theme
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
                DonationItem(
                    theme = theme,
                    index = index,
                    value = value,
                    onPurchaseClick = onPurchaseClick
                )
            }
        }
    }
}
