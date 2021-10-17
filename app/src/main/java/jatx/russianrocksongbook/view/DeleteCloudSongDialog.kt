package jatx.russianrocksongbook.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import jatx.russianrocksongbook.R
import jatx.russianrocksongbook.data.ScalePow
import jatx.russianrocksongbook.viewmodel.MvvmViewModel

@Composable
fun DeleteCloudSongDialog(
    mvvmViewModel: MvvmViewModel,
    onConfirm: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    val theme = mvvmViewModel.settings.theme
    val fontScale = mvvmViewModel.settings.getSpecificFontScale(ScalePow.TEXT)
    val fontSizeTextDp = dimensionResource(id = R.dimen.text_size_12) * fontScale
    val fontSizeTextSp = with(LocalDensity.current) {
        fontSizeTextDp.toSp()
    }

    var secret1 by remember { mutableStateOf("") }
    var secret2 by remember { mutableStateOf("") }

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
        }
    )
}
