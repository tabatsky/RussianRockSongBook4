package jatx.russianrocksongbook.settings.internal.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import jatx.russianrocksongbook.domain.repository.preferences.FontScale
import jatx.russianrocksongbook.domain.repository.preferences.ListenToMusicVariant
import jatx.russianrocksongbook.domain.repository.preferences.Orientation
import jatx.russianrocksongbook.domain.repository.preferences.Theme
import jatx.russianrocksongbook.domain.repository.preferences.colorBlack
import jatx.russianrocksongbook.settings.R
import jatx.spinner.SpinnerState

@Composable
internal fun SettingsBodyLandscape(
    theme: Theme,
    fontSizeLabelSp: TextUnit,
    fontSizeButtonSp: TextUnit,
    valueTheme: MutableState<Theme>,
    spinnerStateTheme: MutableState<SpinnerState>,
    valueFontScale: MutableState<FontScale>,
    spinnerStateFontScale: MutableState<SpinnerState>,
    valueOrientation: MutableState<Orientation>,
    spinnerStateOrientation: MutableState<SpinnerState>,
    valueListenToMusicVariant: MutableState<ListenToMusicVariant>,
    spinnerStateListenToMusicVariant: MutableState<SpinnerState>,
    valueScrollSpeed: MutableFloatState,
    stringScrollSpeed: MutableState<String>,
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
                valueTheme = valueTheme,
                spinnerStateTheme = spinnerStateTheme
            )
            FontScaleRow(
                theme = theme,
                modifier = Modifier
                    .weight(1.0f)
                    .wrapContentHeight(),
                fontSize = fontSizeLabelSp,
                valueFontScale = valueFontScale,
                spinnerStateFontScale = spinnerStateFontScale
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
            OrientationRow(
                theme = theme,
                modifier = Modifier
                    .weight(1.0f)
                    .wrapContentHeight(),
                fontSize = fontSizeLabelSp,
                valueOrientation = valueOrientation,
                spinnerStateOrientation = spinnerStateOrientation
            )
            ListenToMusicVariantRow(
                theme = theme,
                modifier = Modifier
                    .weight(1.0f)
                    .wrapContentHeight(),
                fontSize = fontSizeLabelSp,
                valueListenToMusicVariant = valueListenToMusicVariant,
                spinnerStateListenToMusicVariant = spinnerStateListenToMusicVariant
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
            ScrollSpeedRow(
                theme = theme,
                modifier = Modifier
                    .weight(1.0f)
                    .wrapContentHeight(),
                fontSize = fontSizeLabelSp,
                valueScrollSpeed = valueScrollSpeed,
                stringScrollSpeed = stringScrollSpeed
            )
            Spacer(
                modifier = Modifier
                    .weight(1.0f)
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
