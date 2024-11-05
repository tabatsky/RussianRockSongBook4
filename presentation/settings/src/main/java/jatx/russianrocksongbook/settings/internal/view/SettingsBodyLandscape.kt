package jatx.russianrocksongbook.settings.internal.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import jatx.russianrocksongbook.domain.repository.preferences.Theme
import jatx.russianrocksongbook.domain.repository.preferences.colorBlack
import jatx.russianrocksongbook.settings.R

@Composable
internal fun SettingsBodyLandscape(
    theme: Theme,
    fontSizeLabelSp: TextUnit,
    fontSizeButtonSp: TextUnit,
    onThemePositionChanged: (Int) -> Unit,
    onFontScalePositionChanged: (Int) -> Unit,
    onDefaultArtistValueChanged: (String) -> Unit,
    onOrientationPositionChanged: (Int) -> Unit,
    onListenToMusicVariantPositionChanged: (Int) -> Unit,
    onScrollSpeedValueChanged: (Float) -> Unit,
    onSaveClick: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(4.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ThemeRow(
                theme = theme,
                modifier = Modifier
                    .weight(1.0f)
                    .wrapContentHeight(),
                fontSize = fontSizeLabelSp,
                onPositionChanged = onThemePositionChanged
            )
            FontScaleRow(
                theme = theme,
                modifier = Modifier
                    .weight(1.0f)
                    .wrapContentHeight(),
                fontSize = fontSizeLabelSp,
                onPositionChanged = onFontScalePositionChanged
            )
        }
        Divider(
            color = theme.colorBg,
            thickness = 2.dp
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DefaultArtistRow(
                theme = theme,
                modifier = Modifier
                    .weight(1.0f)
                    .wrapContentHeight(),
                fontSize = fontSizeLabelSp,
                onValueChanged = onDefaultArtistValueChanged
            )
            OrientationRow(
                theme = theme,
                modifier = Modifier
                    .weight(1.0f)
                    .wrapContentHeight(),
                fontSize = fontSizeLabelSp,
                onPositionChanged = onOrientationPositionChanged
            )
        }
        Divider(
            color = theme.colorBg,
            thickness = 2.dp
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ListenToMusicVariantRow(
                theme = theme,
                modifier = Modifier
                    .weight(1.0f)
                    .wrapContentHeight(),
                fontSize = fontSizeLabelSp,
                onPositionChanged = onListenToMusicVariantPositionChanged
            )
            ScrollSpeedRow(
                theme = theme,
                modifier = Modifier
                    .weight(1.0f)
                    .wrapContentHeight(),
                fontSize = fontSizeLabelSp,
                onValueChanged = onScrollSpeedValueChanged
            )
        }
        Divider(
            color = theme.colorBg,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.0f)
        )
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(75.dp),
            colors = ButtonDefaults
                .buttonColors(
                    backgroundColor = theme.colorCommon,
                    contentColor = colorBlack
                ),
            onClick = onSaveClick
        ) {
            Text(
                text = stringResource(id = R.string.apply_settings),
                textAlign = TextAlign.Center,
                fontSize = fontSizeButtonSp
            )
        }
    }
}
