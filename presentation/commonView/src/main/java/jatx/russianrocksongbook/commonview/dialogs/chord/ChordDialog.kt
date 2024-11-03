package jatx.russianrocksongbook.commonview.dialogs.chord

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.dqt.libs.chorddroid.classes.ChordLibrary
import com.dqt.libs.chorddroid.components.ChordTextureView
import jatx.russianrocksongbook.commonview.R
import jatx.russianrocksongbook.commonviewmodel.CommonViewModel
import jatx.russianrocksongbook.domain.repository.preferences.colorBlack

@Composable
fun ChordDialog(
    commonViewModel: CommonViewModel = CommonViewModel.getInstance(),
    chord: String,
    onDismiss: () -> Unit
) {
    val theme = commonViewModel.theme.collectAsState().value
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
            Button(
                modifier = Modifier
                    .background(theme.colorMain)
                    .padding(2.dp)
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults
                    .buttonColors(
                        backgroundColor = theme.colorCommon,
                        contentColor = colorBlack
                    ),
                onClick = {
                    onDismiss()
                }) {
                Text(text = stringResource(id = R.string.close))
            }
        }
    )
}