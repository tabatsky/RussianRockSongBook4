package jatx.russianrocksongbook.localsongs.internal.view.songlist

import android.util.Log
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.lifecycle.viewmodel.compose.viewModel
import jatx.russianrocksongbook.domain.repository.preferences.ScalePow
import jatx.russianrocksongbook.localsongs.R
import jatx.russianrocksongbook.localsongs.internal.viewmodel.LocalViewModel
import jatx.russianrocksongbook.testing.MENU_LAZY_COLUMN

@Composable
internal fun SongListMenuBody(
    onCloseDrawer: () -> Unit
) {
    val localViewModel: LocalViewModel = viewModel()

    val theme = localViewModel.settings.theme
    val artistList by localViewModel.artistList.collectAsState()

    val fontScale = localViewModel.settings.getSpecificFontScale(ScalePow.MENU)
    val fontSizeDp = dimensionResource(id = R.dimen.text_size_20) * fontScale
    val fontSizeSp = with(LocalDensity.current) {
        fontSizeDp.toSp()
    }

    val menuState = rememberLazyListState()

    LazyColumn(
        modifier = Modifier.testTag(MENU_LAZY_COLUMN),
        state = menuState
    ) {
        itemsIndexed(artistList) { index, artist ->
            ArtistItem(
                artist = artist,
                fontSizeSp = fontSizeSp,
                theme = theme,
                onClick = {
                    localViewModel.selectArtist(artist) {
                        localViewModel.selectSong(0)
                    }
                    onCloseDrawer()
                },
                onFocused = {
                    Log.e("focused", artist)
                    menuState.scrollToItem(index)
                }
            )
        }
    }
}