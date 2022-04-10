package jatx.russianrocksongbook.settings.internal.view

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import jatx.russianrocksongbook.domain.repository.preferences.SettingsRepository
import jatx.russianrocksongbook.domain.repository.preferences.Theme
import jatx.russianrocksongbook.settings.R
import jatx.russianrocksongbook.testing.TEXT_FIELD_SCROLL_SPEED

@Composable
internal fun ScrollSpeedRow(
    modifier: Modifier,
    theme: Theme,
    settingsRepository: SettingsRepository,
    fontSize: TextUnit,
    onValueChanged: (Float) -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        var text by remember { mutableStateOf(settingsRepository.scrollSpeed.toString()) }
        Text(
            text = stringResource(id = R.string.scroll_speed),
            modifier = Modifier
                .weight(1.0f)
                .padding(10.dp),
            fontSize = fontSize,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = theme.colorMain
        )
        TextField(
            value = text,
            modifier = Modifier
                .testTag(TEXT_FIELD_SCROLL_SPEED)
                .weight(1.0f)
                .height(60.dp),
            colors = TextFieldDefaults
                .textFieldColors(
                    backgroundColor = theme.colorMain,
                    textColor = theme.colorBg
                ),
            textStyle = TextStyle(
                fontSize = fontSize
            ),
            keyboardOptions = KeyboardOptions
                .Default
                .copy(keyboardType = KeyboardType.Number),
            onValueChange = {
                try {
                    text = if (it.isNotEmpty()) {
                        onValueChanged(it.toFloat())
                        it
                    } else {
                        onValueChanged(0f)
                        ""
                    }
                } catch (e: NumberFormatException) { }
            }
        )
    }
}