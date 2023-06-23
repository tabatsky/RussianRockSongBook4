package jatx.russianrocksongbook.localsongs.internal.view.songtext

import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.TvLazyListState
import jatx.clickablewordstextview.api.Word
import jatx.russianrocksongbook.domain.models.local.Song
import jatx.russianrocksongbook.domain.repository.preferences.Theme
import jatx.russianrocksongbook.localsongs.R
import jatx.russianrocksongbook.localsongs.internal.viewmodel.LocalViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow

const val AUTO_SCROLL_INTERVAL = 250L

@Composable
internal fun SongTextBody(
    W: Dp,
    H: Dp,
    song: Song,
    text: String,
    isEditorMode: Boolean,
    listState: LazyListState,
    tvListState: TvLazyListState,
    fontSizeTextSp: TextUnit,
    fontSizeTitleSp: TextUnit,
    theme: Theme,
    modifier: Modifier,
    isAutoPlayMode: StateFlow<Boolean>,
    dY: Int,
    onTextChange: (String) -> Unit,
    onWordClick: (Word) -> Unit
) {
    val localViewModel = LocalViewModel.getInstance()

    val paddingStart = if (W > H) 20.dp else 0.dp

    Column(
        modifier = modifier
            .padding(start = paddingStart)
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
            SongTextEditor(
                text = text,
                fontSizeTextSp = fontSizeTextSp,
                theme = theme,
                onTextChange = onTextChange
            )
        }

        @Composable
        fun TheViewer() {
            SongTextViewer(
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

        @Composable
        fun SongTextTvLazyColumn() {
            TvLazyColumn(
                state = tvListState,
                modifier = modifier
            ) {
                if (isEditorMode) {
                    item {
                        TheEditor()
                    }
                } else {
                    item {
                        TheViewer()

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
                                autoScroll(tvListState)
                            }
                        }
                    }
                }
            }
        }

        if (localViewModel.isTV) {
            SongTextTvLazyColumn()
        } else {
            SongTextLazyColumn()
        }
    }
}