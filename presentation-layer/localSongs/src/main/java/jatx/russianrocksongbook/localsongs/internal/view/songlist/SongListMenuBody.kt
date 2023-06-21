package jatx.russianrocksongbook.localsongs.internal.view.songlist

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.itemsIndexed
import androidx.tv.foundation.lazy.list.rememberTvLazyListState
import jatx.russianrocksongbook.domain.repository.preferences.ScalePow
import jatx.russianrocksongbook.localsongs.R
import jatx.russianrocksongbook.localsongs.internal.viewmodel.LocalViewModel
import jatx.russianrocksongbook.testing.MENU_LAZY_COLUMN

@Composable
internal fun SongListMenuBody(
    navigationFocusRequester: FocusRequester,
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

    val modifier = Modifier
        .testTag(MENU_LAZY_COLUMN)
        .focusProperties {
            left = navigationFocusRequester
        }

    @Composable
    fun TheItem(artist: String) {
        ArtistItem(
            artist = artist,
            fontSizeSp = fontSizeSp,
            theme = theme,
            onClick = {
                localViewModel.selectArtist(artist = artist)
                onCloseDrawer()
            }
        )
    }

    if (localViewModel.isTV) {
        val menuState = rememberTvLazyListState()
        TvLazyColumn(
            modifier = modifier,
            state = menuState
        ) {
            itemsIndexed(artistList) { _, artist ->
                TheItem(artist)
            }
        }
    } else {
        val menuState = rememberLazyListState()
        LazyColumn(
            modifier = modifier,
            state = menuState
        ) {
            itemsIndexed(artistList) { _, artist ->
                TheItem(artist)
            }
        }
    }
}
