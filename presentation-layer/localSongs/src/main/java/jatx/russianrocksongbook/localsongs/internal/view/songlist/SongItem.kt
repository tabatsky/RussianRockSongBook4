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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import jatx.dpad.dpadFocusable
import jatx.russianrocksongbook.domain.models.local.Song
import jatx.russianrocksongbook.domain.repository.preferences.Theme
import jatx.russianrocksongbook.localsongs.R
import jatx.russianrocksongbook.localsongs.internal.viewmodel.DrawerStateClosed
import jatx.russianrocksongbook.localsongs.internal.viewmodel.LocalViewModel

@Composable
internal fun SongItem(
    song: Song,
    theme: Theme,
    fontSizeSp: TextUnit,
    onClick: () -> Unit,
    onFocused: suspend () -> Unit
) {
    val localViewModel: LocalViewModel = viewModel()

    val drawerState by localViewModel.drawerState.collectAsState()
    val isActive = drawerState == DrawerStateClosed

    val modifier = if (isActive)
        Modifier
            .dpadFocusable(onClick = onClick, onFocused = onFocused)
            .fillMaxWidth()
            .padding(dimensionResource(id = R.dimen.padding_20))
            .clickable {
                onClick()
            }
            .focusProperties { canFocus = true }
            .focusTarget()
    else
        Modifier
            .fillMaxWidth()
            .padding(dimensionResource(id = R.dimen.padding_20))
            .clickable {
                onClick()
            }
            .focusProperties { canFocus = false }
            .focusTarget()

    Text(
        text = song.title,
        modifier = modifier,
        fontSize = fontSizeSp,
        color = theme.colorMain
    )
    Divider(color = theme.colorCommon, thickness = 1.dp)
}
