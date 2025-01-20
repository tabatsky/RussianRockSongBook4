package jatx.russianrocksongbook.cloudsongs.internal.view.cloudsongtext

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
import jatx.russianrocksongbook.cloudsongs.R
import jatx.russianrocksongbook.domain.models.cloud.CloudSong
import jatx.russianrocksongbook.domain.repository.preferences.Theme

@Composable
internal fun CloudSongTextBody(
    W: Dp,
    H: Dp,
    cloudSong: CloudSong,
    listState: LazyListState,
    fontSizeTextSp: TextUnit,
    fontSizeTitleSp: TextUnit,
    theme: Theme,
    modifier: Modifier,
    onWordClick: (String) -> Unit
) {
    val paddingStart = if (W > H) 20.dp else 8.dp

    Column(
        modifier = modifier
            .padding(start = paddingStart, top = 8.dp)
    ) {
        Text(
            text = cloudSong.visibleTitleWithArtistAndRating,
            color = theme.colorMain,
            fontWeight = FontWeight.W700,
            fontSize = fontSizeTitleSp
        )
        Divider(
            color = theme.colorBg,
            thickness = dimensionResource(id = R.dimen.song_text_empty)
        )
        CloudSongTextLazyColumn(
            cloudSong = cloudSong,
            listState = listState,
            fontSizeTextSp = fontSizeTextSp,
            theme = theme,
            modifier = Modifier
                .weight(1.0f),
            onWordClick = onWordClick
        )
    }
}
