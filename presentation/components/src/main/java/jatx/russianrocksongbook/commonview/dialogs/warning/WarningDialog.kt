package jatx.russianrocksongbook.commonview.dialogs.warning

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import jatx.russianrocksongbook.commonview.R
import jatx.russianrocksongbook.commonview.font.toScaledSp
import jatx.russianrocksongbook.commonview.theme.LocalAppTheme
import jatx.russianrocksongbook.domain.repository.preferences.ScalePow
import jatx.russianrocksongbook.testing.TEXT_FIELD_WARNING_COMMENT
import jatx.russianrocksongbook.commonviewmodel.CommonViewModel
import jatx.russianrocksongbook.commonviewmodel.ShowToastWithResource
import jatx.russianrocksongbook.domain.repository.preferences.colorBlack

@Composable
fun WarningDialog(
    commonViewModel: CommonViewModel = CommonViewModel.getInstance(),
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val theme = LocalAppTheme.current

    val fontSizeTextSp = dimensionResource(id = R.dimen.text_size_12)
        .toScaledSp(ScalePow.TEXT)

    var comment by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = {
            onDismiss()
        },
        backgroundColor = theme.colorCommon,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Text(
                    text = stringResource(id = R.string.send_warning_text),
                    color = colorBlack,
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
                            .testTag(TEXT_FIELD_WARNING_COMMENT)
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
                                backgroundColor = theme.colorMain,
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
                    if (comment.isNotEmpty()) {
                        onDismiss()
                        onConfirm(comment)
                    } else {
                        commonViewModel
                            .submitEffect(
                                ShowToastWithResource(R.string.toast_comment_cannot_be_empty))
                    }
                }) {
                Text(text = stringResource(id = R.string.ok))
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
