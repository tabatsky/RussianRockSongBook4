package jatx.russianrocksongbook.commonview.dialogs.warning

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
import androidx.lifecycle.viewmodel.compose.viewModel
import jatx.russianrocksongbook.commonview.R
import jatx.russianrocksongbook.domain.repository.preferences.ScalePow
import jatx.russianrocksongbook.viewmodel.CommonViewModel

@Composable
fun WarningDialog(
    commonViewModel: CommonViewModel = viewModel(),
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val theme = commonViewModel.settings.theme
    val fontScale = commonViewModel.settings.getSpecificFontScale(ScalePow.TEXT)
    val fontSizeTextDp = dimensionResource(id = R.dimen.text_size_12) * fontScale
    val fontSizeTextSp = with(LocalDensity.current) {
        fontSizeTextDp.toSp()
    }

    var comment by remember { mutableStateOf("") }

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
                    text = stringResource(id = R.string.send_warning_text),
                    color = theme.colorBg,
                    fontSize = fontSizeTextSp
                )
                Box(
                    modifier = Modifier
                        .padding(dimensionResource(id = R.dimen.padding_8))
                ) {
                    TextField(
                        value = comment,
                        onValueChange = {
                            comment = it
                        },
                        modifier = Modifier
                            .height(200.dp),
                        label = {
                            Text(
                                text = stringResource(id = R.string.hint_comment),
                                color = theme.colorBg,
                                fontSize = fontSizeTextSp * 0.85f
                            )
                        },
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
                    if (comment.isNotEmpty()) {
                        onDismiss()
                        onConfirm(comment)
                    } else {
                        commonViewModel
                            .showToast(R.string.comment_cannot_be_empty)
                    }
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
