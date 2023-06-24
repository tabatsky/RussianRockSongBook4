package jatx.russianrocksongbook.settings.internal.view

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import jatx.russianrocksongbook.commonview.spinner.Spinner
import jatx.russianrocksongbook.domain.repository.preferences.Theme
import jatx.russianrocksongbook.settings.R
import jatx.russianrocksongbook.settings.internal.viewmodel.SettingsViewModel
import jatx.russianrocksongbook.testing.LISTEN_TO_MUSIC_VARIANT_SPINNER

@Composable
internal fun ListenToMusicVariantRow(
    modifier: Modifier,
    theme: Theme,
    fontSize: TextUnit,
    onPositionChanged: (Int) -> Unit
) {
    val settingsViewModel: SettingsViewModel = hiltViewModel()

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.listen_to_music),
            modifier = Modifier
                .weight(1.0f)
                .padding(10.dp),
            fontSize = fontSize,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = theme.colorMain
        )
        val valueList = stringArrayResource(id = R.array.listen_to_music_variants)
        Spinner(
            modifier = Modifier
                .weight(1.0f)
                .height(60.dp),
            theme = theme,
            testTag = LISTEN_TO_MUSIC_VARIANT_SPINNER,
            fontSize = fontSize,
            valueList = valueList,
            initialPosition = settingsViewModel.valueListenToMusicVariant.value.ordinal,
            onPositionChanged = onPositionChanged,
            positionState = settingsViewModel.positionListenToMusicVariant,
            isExpandedState = settingsViewModel.isExpandedListenToMusicVariant
        )
    }
}