package jatx.russianrocksongbook.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.dqt.libs.chorddroid.components.ChordTextureView
import jatx.russianrocksongbook.R
import jatx.russianrocksongbook.viewmodel.MvvmViewModel

@Composable
fun ChordDialog(
    mvvmViewModel: MvvmViewModel,
    chord: String,
    onDismiss: () -> Unit
) {
    val theme = mvvmViewModel.settings.theme
    val actualChord = chord
        .replace("H", "A")
        .replace("D#", "Eb")
        .replace("A#", "Bb")

    AlertDialog(
        onDismissRequest = {
            onDismiss()
        },
        backgroundColor = theme.colorMain,
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
                        view.setColors(theme.colorBg.toArgb(), theme.colorMain.toArgb())
                        view.drawChord(actualChord)
                    }
                )
            }
        },
        buttons = {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults
                    .buttonColors(
                        backgroundColor = theme.colorCommon,
                        contentColor = theme.colorMain
                    ),
                onClick = {
                    onDismiss()
                }) {
                Text(text = stringResource(id = R.string.close))
            }
        }
    )
}