package jatx.russianrocksongbook.localsongs.internal.view.songlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import jatx.russianrocksongbook.domain.repository.local.*
import jatx.russianrocksongbook.domain.repository.preferences.Theme

@Composable
internal fun ArtistItem(
    artist: String,
    fontSizeSp: TextUnit,
    theme: Theme,
    onClick: () -> Unit
) {
    val isBold =
        (listOf(
            ARTIST_FAVORITE,
            ARTIST_ADD_ARTIST,
            ARTIST_ADD_SONG,
            ARTIST_CLOUD_SONGS,
            ARTIST_DONATION
        ).contains(artist))
    Text(
        text = artist,
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .clickable {
                onClick()
            },
        fontWeight = if (isBold) FontWeight.W700 else FontWeight.W400,
        fontSize = fontSizeSp,
        color = theme.colorBg
    )
    Divider(color = theme.colorCommon, thickness = 1.dp)
}
