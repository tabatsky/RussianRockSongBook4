package jatx.russianrocksongbook.commonsongtext.view

import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import jatx.russianrocksongbook.commonsongtext.R
import jatx.russianrocksongbook.domain.models.local.Song
import jatx.russianrocksongbook.domain.repository.preferences.Theme
import kotlinx.coroutines.delay

const val AUTO_SCROLL_INTERVAL = 250L

@Composable
internal fun CommonSongTextBody(
    W: Dp,
    H: Dp,
    song: Song,
    text: String,
    isEditorMode: Boolean,
    listState: LazyListState,
    fontSizeTextSp: TextUnit,
    fontSizeTitleSp: TextUnit,
    theme: Theme,
    modifier: Modifier,
    isAutoPlayMode: Boolean,
    dY: Int,
    onTextChange: (String) -> Unit,
    onWordClick: (String) -> Unit
) {
    val paddingStart = if (W > H) 20.dp else 8.dp

    Column(
        modifier = modifier
            .padding(start = paddingStart, top = 8.dp)
    ) {
        Text(
            text = "${song.title} (${song.artist})",
            color = theme.colorMain,
            fontWeight = FontWeight.W700,
            fontSize = fontSizeTitleSp
        )
        Divider(
            color = theme.colorBg,
            thickness = dimensionResource(id = R.dimen.song_text_empty)
        )

        @Composable
        fun TheEditor() {
            CommonSongTextEditor(
                text = text,
                fontSizeTextSp = fontSizeTextSp,
                theme = theme,
                onTextChange = onTextChange
            )
        }

        @Composable
        fun TheViewer() {
            CommonSongTextViewer(
                song = song,
                theme = theme,
                fontSizeTextSp = fontSizeTextSp,
                onWordClick = onWordClick
            )
        }

        @Composable
        fun SongTextLazyColumn() {
            LazyColumn(
                state = listState,
                modifier = modifier
            ) {
                if (isEditorMode) {
                    item {
                        TheEditor()
                    }
                } else {
                    item {
                        TheViewer()

                        tailrec suspend fun autoScroll(listState: LazyListState) {
                            listState.scroll(MutatePriority.PreventUserInput) {
                                scrollBy(dY.toFloat())
                            }

                            delay(AUTO_SCROLL_INTERVAL)
                            autoScroll(listState)
                        }

                        if (isAutoPlayMode) {
                            LaunchedEffect(Unit) {
                                autoScroll(listState)
                            }
                        }
                    }
                }
            }
        }

        SongTextLazyColumn()
    }
}