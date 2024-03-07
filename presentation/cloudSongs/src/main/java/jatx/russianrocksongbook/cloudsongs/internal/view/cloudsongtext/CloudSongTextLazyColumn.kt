package jatx.russianrocksongbook.cloudsongs.internal.view.cloudsongtext

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import jatx.clickablewordstextcompose.api.Word
import jatx.russianrocksongbook.domain.models.cloud.CloudSong
import jatx.russianrocksongbook.domain.repository.preferences.Theme

@Composable
internal fun CloudSongTextLazyColumn(
    cloudSong: CloudSong,
    listState: LazyListState,
    fontSizeTextSp: TextUnit,
    theme: Theme,
    modifier: Modifier,
    onWordClick: (Word) -> Unit
) {
    LazyColumn(
        state = listState,
        modifier = modifier
    ) {
        item {
            CloudSongTextViewer(
                cloudSong = cloudSong,
                theme = theme,
                fontSizeTextSp = fontSizeTextSp,
                onWordClick = onWordClick
            )
        }
    }
}