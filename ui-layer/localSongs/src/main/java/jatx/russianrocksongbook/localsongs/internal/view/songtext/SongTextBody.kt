package jatx.russianrocksongbook.localsongs.internal.view.songtext

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import jatx.clickablewordstextview.api.Word
import jatx.russianrocksongbook.domain.models.local.Song
import jatx.russianrocksongbook.domain.repository.preferences.Theme
import jatx.russianrocksongbook.localsongs.R
import kotlinx.coroutines.flow.StateFlow

@Composable
internal fun SongTextBody(
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
    isAutoPlayMode: StateFlow<Boolean>,
    dY: Int,
    onTextChange: (String) -> Unit,
    onWordClick: (Word) -> Unit
) {
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
        SongTextLazyColumn(
            song = song,
            text = text,
            isEditorMode = isEditorMode,
            listState = listState,
            fontSizeTextSp = fontSizeTextSp,
            theme = theme,
            modifier = Modifier
                .weight(1.0f),
            isAutoPlayMode = isAutoPlayMode,
            dY = dY,
            onTextChange = onTextChange,
            onWordClick = onWordClick
        )
    }
}