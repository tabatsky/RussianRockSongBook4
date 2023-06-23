package jatx.russianrocksongbook.settings.internal.view

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import jatx.russianrocksongbook.commonview.spinner.Spinner
import jatx.russianrocksongbook.domain.repository.local.ARTIST_ADD_ARTIST
import jatx.russianrocksongbook.domain.repository.local.ARTIST_ADD_SONG
import jatx.russianrocksongbook.domain.repository.local.ARTIST_CLOUD_SONGS
import jatx.russianrocksongbook.domain.repository.local.ARTIST_DONATION
import jatx.russianrocksongbook.domain.repository.preferences.SettingsRepository
import jatx.russianrocksongbook.domain.repository.preferences.Theme
import jatx.russianrocksongbook.settings.R
import jatx.russianrocksongbook.settings.internal.viewmodel.SettingsViewModel
import jatx.russianrocksongbook.testing.DEFAULT_ARTIST_SPINNER

// TODO: remove settingsRepository from param

@Composable
internal fun DefaultArtistRow(
    modifier: Modifier,
    theme: Theme,
    settingsRepository: SettingsRepository,
    fontSize: TextUnit,
    onValueChanged: (String) -> Unit
) {
    val settingsViewModel: SettingsViewModel = hiltViewModel()

    val artistList by settingsViewModel.artistList.collectAsState()
    val artists = ArrayList(artistList).apply {
        remove(ARTIST_CLOUD_SONGS)
        remove(ARTIST_ADD_SONG)
        remove(ARTIST_ADD_ARTIST)
        remove(ARTIST_DONATION)
    }
    val initialPosition = artists.indexOf(settingsViewModel.valueDefaultArtist.value)

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.def_artist),
            modifier = Modifier
                .weight(1.0f)
                .padding(10.dp),
            fontSize = fontSize,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = theme.colorMain
        )
        val valueList = artists.toTypedArray()
        Spinner(
            modifier = Modifier
                .weight(1.0f)
                .height(60.dp),
            theme = theme,
            testTag = DEFAULT_ARTIST_SPINNER,
            fontSize = fontSize,
            valueList = valueList,
            initialPosition = initialPosition,
            onPositionChanged = {
                onValueChanged(artists[it])
            },
            positionState = settingsViewModel.positionDefaultArtist,
            isExpandedState = settingsViewModel.isExpandedDefaultArtist
        )
    }
}