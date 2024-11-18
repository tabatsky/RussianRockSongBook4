package jatx.russianrocksongbook.commonsongtext.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.TextUnit
import jatx.russianrocksongbook.domain.repository.preferences.Theme
import jatx.russianrocksongbook.testing.SONG_TEXT_EDITOR

@Composable
internal fun CommonSongTextEditor(
    text: String,
    fontSizeTextSp: TextUnit,
    theme: Theme,
    onTextChange: (String) -> Unit
) {
    BasicTextField(
        modifier = Modifier
            .testTag(SONG_TEXT_EDITOR)
            .fillMaxWidth(),
        value = text,
        onValueChange = onTextChange,
        textStyle = TextStyle(
            fontFamily = FontFamily.Monospace,
            fontSize = fontSizeTextSp,
            color = theme.colorMain
        ),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .background(theme.colorBg)
            ) {
                innerTextField()  //<-- Add this
            }
        },
        cursorBrush = SolidColor(theme.colorCommon)
    )
}
