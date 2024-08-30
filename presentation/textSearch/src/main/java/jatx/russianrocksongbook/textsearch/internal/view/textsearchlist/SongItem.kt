package jatx.russianrocksongbook.textsearch.internal.view.textsearchlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import jatx.russianrocksongbook.domain.models.local.Song
import jatx.russianrocksongbook.domain.repository.preferences.Theme
import jatx.russianrocksongbook.textsearch.R

@Composable
internal fun SongItem(
    song: Song,
    theme: Theme,
    fontSizeArtistSp: TextUnit,
    fontSizeSongTitleSp: TextUnit,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable {
                onClick()
            }
    ) {
        Text(
            text = song.title,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = dimensionResource(id = R.dimen.padding_8),
                    bottom = dimensionResource(id = R.dimen.padding_8),
                    start = dimensionResource(id = R.dimen.padding_20)
                ),
            fontSize = fontSizeSongTitleSp,
            color = theme.colorMain
        )
        Text(
            text = song.artist,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = dimensionResource(id = R.dimen.padding_8),
                    bottom = dimensionResource(id = R.dimen.padding_8),
                    start = dimensionResource(id = R.dimen.padding_20)
                ),
            fontSize = fontSizeArtistSp,
            fontStyle = FontStyle.Italic,
            color = theme.colorMain
        )
        Divider(color = theme.colorCommon, thickness = 1.dp)
    }
}