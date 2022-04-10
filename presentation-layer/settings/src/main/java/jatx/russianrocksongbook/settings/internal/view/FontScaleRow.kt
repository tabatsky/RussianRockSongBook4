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
import jatx.russianrocksongbook.commonview.spinner.Spinner
import jatx.russianrocksongbook.domain.repository.preferences.SettingsRepository
import jatx.russianrocksongbook.domain.repository.preferences.Theme
import jatx.russianrocksongbook.settings.R
import jatx.russianrocksongbook.testing.FONT_SCALE_SPINNER

@Composable
internal fun FontScaleRow(
    modifier: Modifier,
    theme: Theme,
    settingsRepository: SettingsRepository,
    fontSize: TextUnit,
    onPositionChanged: (Int) -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.font_scale),
            modifier = Modifier
                .weight(1.0f)
                .padding(10.dp),
            fontSize = fontSize,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = theme.colorMain
        )
        val valueList = stringArrayResource(id = R.array.font_scale_list)
        Spinner(
            modifier = Modifier
                .weight(1.0f)
                .height(60.dp),
            theme = theme,
            testTag = FONT_SCALE_SPINNER,
            fontSize = fontSize,
            valueList = valueList,
            initialPosition = settingsRepository.commonFontScaleEnum.ordinal,
            onPositionChanged = onPositionChanged
        )
    }
}
