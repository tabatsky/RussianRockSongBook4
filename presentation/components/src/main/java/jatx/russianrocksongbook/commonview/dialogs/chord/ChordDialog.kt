package jatx.russianrocksongbook.commonview.dialogs.chord

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.dqt.libs.chorddroid.classes.ChordLibrary
import com.dqt.libs.chorddroid.components.ChordTextureView
import jatx.russianrocksongbook.commonview.R
import jatx.russianrocksongbook.commonview.font.toScaledSp
import jatx.russianrocksongbook.commonview.theme.LocalAppTheme
import jatx.russianrocksongbook.domain.repository.preferences.ScalePow
import jatx.russianrocksongbook.domain.repository.preferences.colorBlack

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChordDialog(
    chord: String,
    onDismiss: () -> Unit
) {
    val theme = LocalAppTheme.current

    val fontSizeButtonSp = dimensionResource(R.dimen.text_size_16)
        .toScaledSp(ScalePow.BUTTON)

    var actualChord = chord
    for (key in ChordLibrary.chordMappings.keys) {
        actualChord = actualChord.replace(key, ChordLibrary.chordMappings[key] ?: "")
    }

    AlertDialog(
        onDismissRequest = {
            onDismiss()
        },
        backgroundColor = theme.colorCommon,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AndroidView(
                    modifier = Modifier
                        .width(200.dp)
                        .height(200.dp),
                    factory = { context ->
                        ChordTextureView(context)
                    },
                    update = { view ->
                        view.setColors(theme.colorMain.toArgb(), theme.colorBg.toArgb())
                        view.drawChord(actualChord)
                    }
                )
            }
        },
        buttons = {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(dimensionResource(R.dimen.padding_20))
                        .clickable {
                            onDismiss()
                        },
                    color = colorBlack,
                    fontWeight = FontWeight.W500,
                    fontSize = fontSizeButtonSp,
                    text = stringResource(id = R.string.close)
                )
            }
        }
    )
}