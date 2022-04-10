package jatx.russianrocksongbook.localsongs.internal.view.songlist

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.lifecycle.viewmodel.compose.viewModel
import jatx.russianrocksongbook.commonview.stub.CommonSongListStub
import jatx.russianrocksongbook.domain.repository.preferences.ScalePow
import jatx.russianrocksongbook.localsongs.R
import jatx.russianrocksongbook.localsongs.internal.viewmodel.LocalViewModel
import jatx.russianrocksongbook.testing.SONG_LIST_LAZY_COLUMN
import jatx.russianrocksongbook.testing.TestingConfig
import jatx.russianrocksongbook.viewmodel.CurrentScreenVariant
import kotlinx.coroutines.delay

@Composable
internal fun SongListBody() {
    val localViewModel: LocalViewModel = viewModel()

    val theme = localViewModel.settings.theme

    val songList by localViewModel.currentSongList.collectAsState()

    val fontScale = localViewModel.settings.getSpecificFontScale(ScalePow.TEXT)
    val fontSizeDp = dimensionResource(id = R.dimen.text_size_20) * fontScale
    val fontSizeSp = with(LocalDensity.current) {
        fontSizeDp.toSp()
    }

    if (songList.isNotEmpty()) {
        val listState = rememberLazyListState()
        val wasOrientationChanged by localViewModel.wasOrientationChanged.collectAsState()
        val needScroll by localViewModel.needScroll.collectAsState()
        LazyColumn(
            modifier = Modifier.testTag(SONG_LIST_LAZY_COLUMN),
            state = listState
        ) {
            itemsIndexed(songList) { index, song ->
                SongItem(song, theme, fontSizeSp) {
                    println("selected: ${song.artist} - ${song.title}")
                    localViewModel.selectSong(index)
                    localViewModel.selectScreen(CurrentScreenVariant.SONG_TEXT)
                }
            }
            if (!wasOrientationChanged && !needScroll) {
                localViewModel.updateScrollPosition(listState.firstVisibleItemIndex)
            }
        }
        if (needScroll) {
            val scrollPosition by localViewModel.scrollPosition.collectAsState()
            LaunchedEffect(Unit) {
                if (TestingConfig.isTesting) {
                    delay(100L)
                }
                listState.scrollToItem(scrollPosition)
                localViewModel.updateNeedScroll(false)
            }
        }
    } else {
        CommonSongListStub(fontSizeSp, theme)
    }
}
