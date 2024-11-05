package jatx.russianrocksongbook.localsongs.internal.view.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import jatx.russianrocksongbook.commonview.font.toScaledSp
import jatx.russianrocksongbook.commonview.theme.LocalAppTheme
import jatx.russianrocksongbook.domain.repository.preferences.ScalePow
import jatx.russianrocksongbook.domain.repository.preferences.colorBlack
import jatx.russianrocksongbook.localsongs.R

@Composable
internal fun VoiceHelpDialog(
    onConfirm: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    val theme = LocalAppTheme.current

    val fontSizeTextSp = dimensionResource(id = R.dimen.text_size_16)
        .toScaledSp(ScalePow.TEXT)

    AlertDialog(
        onDismissRequest = {
            onDismiss()
        },
        backgroundColor = theme.colorMain,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Text(
                    text = stringResource(id = R.string.dialog_voice_help),
                    color = theme.colorBg,
                    fontSize = fontSizeTextSp
                )
            }
        },
        buttons = {
            Button(
                modifier = Modifier
                    .background(colorBlack)
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
                    onConfirm(false)
                }) {
                Text(text = stringResource(id = R.string.accept))
            }
            Button(
                modifier = Modifier
                    .background(colorBlack)
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
                    onConfirm(true)
                }) {
                Text(text = stringResource(id = R.string.dont_ask_more))
            }
            Button(
                modifier = Modifier
                    .background(colorBlack)
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
                Text(text = stringResource(id = R.string.cancel))
            }
        }
    )
}