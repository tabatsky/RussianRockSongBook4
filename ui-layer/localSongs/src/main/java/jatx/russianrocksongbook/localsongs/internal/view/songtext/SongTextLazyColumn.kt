package jatx.russianrocksongbook.localsongs.internal.view.songtext

import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import jatx.clickablewordstextview.api.Word
import jatx.russianrocksongbook.domain.models.local.Song
import jatx.russianrocksongbook.domain.repository.preferences.Theme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow

private const val AUTO_SCROLL_INTERVAL = 250L

@Composable
internal fun SongTextLazyColumn(
    song: Song,
    text: String,
    isEditorMode: Boolean,
    listState: LazyListState,
    fontSizeTextSp: TextUnit,
    theme: Theme,
    modifier: Modifier,
    isAutoPlayMode: StateFlow<Boolean>,
    dY: Int,
    onTextChange: (String) -> Unit,
    onWordClick: (Word) -> Unit
) {
    LazyColumn(
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

                tailrec suspend fun autoScroll(listState: LazyListState) {
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