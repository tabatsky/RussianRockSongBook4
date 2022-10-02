package jatx.russianrocksongbook.localsongs.internal.view.songlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import jatx.dpad.dpadFocusable
import jatx.russianrocksongbook.domain.repository.local.*
import jatx.russianrocksongbook.domain.repository.preferences.Theme
import jatx.russianrocksongbook.localsongs.internal.viewmodel.DrawerStateOpened
import jatx.russianrocksongbook.localsongs.internal.viewmodel.LocalViewModel

@Composable
internal fun ArtistItem(
    artist: String,
    fontSizeSp: TextUnit,
    theme: Theme,
    onClick: () -> Unit,
    onFocused: suspend () -> Unit
) {
    val localViewModel: LocalViewModel = viewModel()

    val drawerState by localViewModel.drawerState.collectAsState()
    val isActive = drawerState == DrawerStateOpened

    val modifier = if (isActive)
        Modifier
            .dpadFocusable(onClick = onClick, onFocused = onFocused)
            .fillMaxWidth()
            .padding(5.dp)
            .clickable {
                onClick()
            }
            .focusProperties { canFocus = true }
            .focusTarget()
    else
        Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .clickable {
                onClick()
            }
            .focusProperties { canFocus = false }
            .focusTarget()

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
        modifier = modifier,
        fontWeight = if (isBold) FontWeight.W700 else FontWeight.W400,
        fontSize = fontSizeSp,
        color = theme.colorBg
    )
    Divider(color = theme.colorCommon, thickness = 1.dp)
}
