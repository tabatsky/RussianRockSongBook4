package jatx.russianrocksongbook.cloudsongs.internal.view.cloudsearch

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
import jatx.russianrocksongbook.cloudsongs.R
import jatx.russianrocksongbook.domain.models.cloud.CloudSong
import jatx.russianrocksongbook.domain.repository.preferences.Theme

@Composable
internal fun CloudSongItem(
    cloudSong: CloudSong,
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
            text = cloudSong.visibleTitleWithRating,
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
            text = cloudSong.artist,
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