package jatx.russianrocksongbook.localsongs.internal.view.songlist

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
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
    val labelModifier = Modifier
        .fillMaxWidth()
        .padding(5.dp)
        .clickable {
            onGroupClick()
        }

    val animationSpec = tween<IntSize>(durationMillis = 300, easing = LinearOutSlowInEasing)

    Column(
        modifier = Modifier
            .animateContentSize(
                animationSpec = animationSpec
            )
    ) {
        Text(
            text = artistGroup,
            modifier = labelModifier,
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

    val artistModifier = (if (isPredefined) {
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
        modifier = artistModifier,
        fontWeight = if (isPredefined) FontWeight.W700 else FontWeight.W400,
        fontSize = fontSizeSp,
        color = theme.colorBg
    )
    Divider(color = theme.colorCommon, thickness = 1.dp)
}
