package jatx.russianrocksongbook.commonview.dialogs.warning

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import jatx.russianrocksongbook.commonviewmodel.ShowToastWithResource
import jatx.russianrocksongbook.commonviewmodel.UIEffect
import jatx.russianrocksongbook.domain.repository.preferences.ScalePow
import jatx.russianrocksongbook.domain.repository.preferences.colorBlack
import jatx.russianrocksongbook.testing.TEXT_FIELD_WARNING_COMMENT

@Composable
internal fun WarningDialogContent(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
    submitEffect: (UIEffect) -> Unit,
    initialComment: String = ""
) {
    val theme = LocalAppTheme.current

    val fontSizeTextSp = dimensionResource(id = R.dimen.text_size_12)
        .toScaledSp(ScalePow.TEXT)

    var comment by rememberSaveable { mutableStateOf(initialComment) }

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
                        submitEffect(
                            ShowToastWithResource(R.string.toast_comment_cannot_be_empty)
                        )
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