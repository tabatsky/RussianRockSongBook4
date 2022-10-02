package jatx.russianrocksongbook.localsongs.internal.view.songlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import jatx.dpad.dpadFocusable
import jatx.russianrocksongbook.domain.models.local.Song
import jatx.russianrocksongbook.domain.repository.preferences.Theme
import jatx.russianrocksongbook.localsongs.R

@Composable
internal fun SongItem(
    song: Song,
    theme: Theme,
    fontSizeSp: TextUnit,
    onClick: () -> Unit,
    onFocused: suspend () -> Unit,
    isSongListActive: Boolean
) {
//    val modifier = if (isSongListActive)
//        Modifier.dpadFocusable(onClick = onClick, onFocused = onFocused)
//    else
//        Modifier.focusProperties {
//            canFocus = false
//        }
    val modifier = Modifier.dpadFocusable(onClick = onClick, onFocused = onFocused)
    Text(
        text = song.title,
        modifier = modifier
            .fillMaxWidth()
            .padding(dimensionResource(id = R.dimen.padding_20))
            .clickable {
                onClick()
            },
        fontSize = fontSizeSp,
        color = theme.colorMain
    )
    Divider(color = theme.colorCommon, thickness = 1.dp)
}
