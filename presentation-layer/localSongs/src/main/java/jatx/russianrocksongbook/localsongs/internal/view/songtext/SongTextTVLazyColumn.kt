package jatx.russianrocksongbook.localsongs.internal.view.songtext

import androidx.compose.foundation.MutatePriority
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.TvLazyListState
import jatx.clickablewordstextview.api.Word
import jatx.russianrocksongbook.domain.models.local.Song
import jatx.russianrocksongbook.domain.repository.preferences.Theme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow

@Composable
internal fun SongTextTvLazyColumn(
    song: Song,
    text: String,
    isEditorMode: Boolean,
    listState: TvLazyListState,
    fontSizeTextSp: TextUnit,
    theme: Theme,
    modifier: Modifier,
    isAutoPlayMode: StateFlow<Boolean>,
    dY: Int,
    onTextChange: (String) -> Unit,
    onWordClick: (Word) -> Unit
) {
    TvLazyColumn(
        state = listState,
        modifier = modifier
    ) {
        if (isEditorMode) {
            item {
                SongTextEditor(
                    text = text,
                    fontSizeTextSp = fontSizeTextSp,
                    theme = theme,
                    onTextChange = onTextChange
                )
            }
        } else {
            item {
                SongTextViewer(
                    song = song,
                    theme = theme,
                    fontSizeTextSp = fontSizeTextSp,
                    onWordClick = onWordClick
                )

                val needToScroll by isAutoPlayMode.collectAsState()

                tailrec suspend fun autoScroll(listState: TvLazyListState) {
                    listState.scroll(MutatePriority.PreventUserInput) {
                        scrollBy(dY.toFloat())
                    }

                    delay(AUTO_SCROLL_INTERVAL)
                    autoScroll(listState)
                }

                if (needToScroll) {
                    LaunchedEffect(Unit) {
                        autoScroll(listState)
                    }
                }
            }
        }
    }
}