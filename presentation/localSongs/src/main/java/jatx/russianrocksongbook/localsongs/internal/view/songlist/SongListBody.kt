package jatx.russianrocksongbook.localsongs.internal.view.songlist

import android.util.Log
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import jatx.russianrocksongbook.commonsongtext.viewmodel.UpdateSongListNeedScroll
import jatx.russianrocksongbook.commonsongtext.viewmodel.UpdateSongListScrollPosition
import jatx.russianrocksongbook.commonview.font.toScaledSp
import jatx.russianrocksongbook.commonview.stub.CommonSongListStub
import jatx.russianrocksongbook.commonview.theme.LocalAppTheme
import jatx.russianrocksongbook.commonviewmodel.SelectScreen
import jatx.russianrocksongbook.commonviewmodel.UIAction
import jatx.russianrocksongbook.domain.models.local.Song
import jatx.russianrocksongbook.domain.repository.preferences.ScalePow
import jatx.russianrocksongbook.localsongs.R
import jatx.russianrocksongbook.testing.SONG_LIST_LAZY_COLUMN
import jatx.russianrocksongbook.testing.TestingConfig
import jatx.russianrocksongbook.navigation.ScreenVariant
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce

@OptIn(FlowPreview::class)
@Composable
internal fun SongListBody(
    navigationFocusRequester: FocusRequester,
    songList: List<Song>,
    scrollPosition: Int,
    needScroll: Boolean,
    submitAction: (UIAction) -> Unit
) {
    val theme = LocalAppTheme.current

    val fontSizeSp = dimensionResource(id = R.dimen.text_size_20)
        .toScaledSp(ScalePow.TEXT)

    @Composable
    fun TheItem(index: Int, song: Song) {
        SongItem(
            song = song,
            theme = theme,
            fontSizeSp = fontSizeSp,
            onClick = {
                Log.e("SongListBody", "selected: ${song.artist} - ${song.title}")
                submitAction(
                    SelectScreen(
                        ScreenVariant
                            .SongText(index)
                    )
                )
            }
        )
    }

    @Composable
    fun ScrollEffect(
        onPerformScroll: suspend (Int) -> Unit,
        getFirstVisibleItemIndex: () -> Int
    ) {
        LaunchedEffect(needScroll) {
            if (needScroll) {
                if (TestingConfig.isUITesting) {
                    delay(100L)
                }
                if (scrollPosition >= 0) onPerformScroll(scrollPosition)
                submitAction(UpdateSongListNeedScroll(false))
            } else {
                snapshotFlow {
                    getFirstVisibleItemIndex()
                }.debounce(200L).collectLatest {
                    submitAction(UpdateSongListScrollPosition(it))
                }
            }
        }
    }

    if (songList.isNotEmpty()) {
        val modifier = Modifier
            .testTag(SONG_LIST_LAZY_COLUMN)
            .focusProperties {
                left = navigationFocusRequester
            }

        val listState = rememberLazyListState()
        LazyColumn(
            modifier = modifier,
            state = listState
        ) {
            itemsIndexed(songList) { index, song ->
                TheItem(index, song)
            }
        }
        ScrollEffect(
            onPerformScroll = { listState.scrollToItem(it) },
            getFirstVisibleItemIndex = { listState.firstVisibleItemIndex }
        )
    } else {
        CommonSongListStub(fontSizeSp, theme)
    }
}
