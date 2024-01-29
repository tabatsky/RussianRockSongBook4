package jatx.russianrocksongbook.cloudsongs.internal.view.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import jatx.russianrocksongbook.cloudsongs.R
import jatx.russianrocksongbook.cloudsongs.internal.viewmodel.CloudViewModel
import jatx.russianrocksongbook.commonview.font.toScaledSp
import jatx.russianrocksongbook.commonview.theme.LocalAppTheme
import jatx.russianrocksongbook.domain.repository.preferences.ScalePow

@Composable
internal fun DeleteCloudSongDialog(
    onConfirm: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    val cloudViewModel = CloudViewModel.getInstance()
    val theme = LocalAppTheme.current
    val fontSizeTextSp = dimensionResource(id = R.dimen.text_size_12)
        .toScaledSp(ScalePow.TEXT)

    var secret1 by rememberSaveable { mutableStateOf("") }
    var secret2 by rememberSaveable { mutableStateOf("") }

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
                TextField(
                    value = secret1,
                    onValueChange = {
                        secret1 = it
                    },
                    modifier = Modifier
                        .wrapContentHeight(),
                    colors = TextFieldDefaults
                        .textFieldColors(
                            backgroundColor = theme.colorCommon,
                            textColor = theme.colorBg
                        ),
                    textStyle = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = fontSizeTextSp
                    )
                )
                TextField(
                    value = secret2,
                    onValueChange = {
                        secret2 = it
                    },
                    modifier = Modifier
                        .wrapContentHeight(),
                    colors = TextFieldDefaults
                        .textFieldColors(
                            backgroundColor = theme.colorCommon,
                            textColor = theme.colorBg
                        ),
                    textStyle = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = fontSizeTextSp
                    )
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
                    onConfirm(secret1, secret2)
                }) {
                Text(text = stringResource(id = R.string.ok))
            }
            Divider(
                color = theme.colorMain,
                thickness = 2.dp
            )
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
                Text(text = stringResource(id = R.string.cancel))
            }
            Divider(
                color = theme.colorMain,
                thickness = 2.dp
            )
        }
    )
}
