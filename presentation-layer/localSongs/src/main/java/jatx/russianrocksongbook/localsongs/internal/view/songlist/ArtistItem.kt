package jatx.russianrocksongbook.localsongs.internal.view.songlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import jatx.russianrocksongbook.domain.repository.local.*
import jatx.russianrocksongbook.domain.repository.preferences.Theme

@Composable
internal fun ArtistGroupItem(
    artistGroup: String,
    expandedList: List<String>,
    fontSizeSp: TextUnit,
    theme: Theme,
    onGroupClick: () -> Unit,
    onArtistClick: (String) -> Unit
) {
    val modifier = Modifier
        .fillMaxWidth()
        .padding(5.dp)
        .clickable {
            onGroupClick()
        }

    Column {
        Text(
            text = artistGroup,
            modifier = modifier,
            fontWeight = FontWeight.W500,
            fontSize = fontSizeSp,
            color = theme.colorBg
        )
        Divider(color = theme.colorCommon, thickness = 1.dp)
        expandedList.forEach {
            ArtistItem(
                artist = it,
                fontSizeSp = fontSizeSp,
                theme = theme,
                onClick = {
                    onArtistClick(it)
                }
            )
        }
    }
}

@Composable
internal fun ArtistItem(
    artist: String,
    fontSizeSp: TextUnit,
    theme: Theme,
    onClick: () -> Unit
) {
    val isPredefined = predefinedArtistList.contains(artist)

    val modifier = (if (isPredefined) {
        Modifier
            .fillMaxWidth()
            .padding(5.dp)
            } else {
        Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 5.dp, top = 5.dp, bottom = 5.dp)
            })
        .clickable {
            onClick()
        }

    Text(
        text = artist,
        modifier = modifier,
        fontWeight = if (isPredefined) FontWeight.W700 else FontWeight.W400,
        fontSize = fontSizeSp,
        color = theme.colorBg
    )
    Divider(color = theme.colorCommon, thickness = 1.dp)
}
