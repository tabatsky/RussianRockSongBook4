package jatx.russianrocksongbook.localsongs.internal.view.songlist

import android.util.Log
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
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
import jatx.russianrocksongbook.commonview.stub.CommonSongListStub
import jatx.russianrocksongbook.domain.repository.preferences.ScalePow
import jatx.russianrocksongbook.localsongs.R
import jatx.russianrocksongbook.localsongs.internal.viewmodel.LocalViewModel
import jatx.russianrocksongbook.testing.SONG_LIST_LAZY_COLUMN
import jatx.russianrocksongbook.testing.TestingConfig
import jatx.russianrocksongbook.viewmodel.CurrentScreenVariant
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@Composable
internal fun SongListBody(
    navigationFocusRequester: FocusRequester
) {
    val localViewModel: LocalViewModel = viewModel()

    val theme = localViewModel.settings.theme

    val songList by localViewModel.currentSongList.collectAsState()

    val scrollPositionWrapper by localViewModel.scrollPosition.collectAsState()
    val scrollPosition = scrollPositionWrapper.value
    val needScrollWrapper by localViewModel.needScroll.collectAsState()
    val needScroll = needScrollWrapper.value

    val fontScale = localViewModel.settings.getSpecificFontScale(ScalePow.TEXT)
    val fontSizeDp = dimensionResource(id = R.dimen.text_size_20) * fontScale
    val fontSizeSp = with(LocalDensity.current) {
        fontSizeDp.toSp()
    }

    if (songList.isNotEmpty()) {
        val modifier = Modifier
            .testTag(SONG_LIST_LAZY_COLUMN)
            .focusProperties {
                left = navigationFocusRequester
            }
        if (localViewModel.isTV) {
            val listState = rememberTvLazyListState()
            TvLazyColumn(
                modifier = modifier,
                state = listState
            ) {
                itemsIndexed(songList) { index, song ->
                    SongItem(
                        song = song,
                        theme = theme,
                        fontSizeSp = fontSizeSp,
                        onClick = {
                            Log.e("SongListBody", "selected: ${song.artist} - ${song.title}")
                            localViewModel.selectSong(index)
                            localViewModel.selectScreen(CurrentScreenVariant.SONG_TEXT)
                        }
                    )
                }
            }
            LaunchedEffect(needScroll) {
                if (needScroll) {
                    if (TestingConfig.isTesting) {
                        delay(100L)
                    }
                    listState.scrollToItem(scrollPosition)
                    localViewModel.updateNeedScroll(false)
                } else {
                    delay(500L)
                    snapshotFlow {
                        listState.firstVisibleItemIndex
                    }.collectLatest {
                        localViewModel.updateScrollPosition(it)
                    }
                }
            }
        } else {
            val listState = rememberLazyListState()
            LazyColumn(
                modifier = modifier,
                state = listState
            ) {
                itemsIndexed(songList) { index, song ->
                    SongItem(
                        song = song,
                        theme = theme,
                        fontSizeSp = fontSizeSp,
                        onClick = {
                            Log.e("SongListBody", "selected: ${song.artist} - ${song.title}")
                            localViewModel.selectSong(index)
                            localViewModel.selectScreen(CurrentScreenVariant.SONG_TEXT)
                        }
                    )
                }
            }
            LaunchedEffect(needScroll) {
                if (needScroll) {
                    if (TestingConfig.isTesting) {
                        delay(100L)
                    }
                    listState.scrollToItem(scrollPosition)
                    localViewModel.updateNeedScroll(false)
                } else {
                    snapshotFlow {
                        listState.firstVisibleItemIndex
                    }.collectLatest {
                        localViewModel.updateScrollPosition(it)
                    }
                }
            }
        }
    } else {
        CommonSongListStub(fontSizeSp, theme)
    }
}
