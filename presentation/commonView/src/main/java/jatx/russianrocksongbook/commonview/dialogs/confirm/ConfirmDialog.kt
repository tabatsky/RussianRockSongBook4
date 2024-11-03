package jatx.russianrocksongbook.commonview.dialogs.confirm

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import jatx.russianrocksongbook.commonview.R
import jatx.russianrocksongbook.commonview.font.toScaledSp
import jatx.russianrocksongbook.domain.repository.preferences.ScalePow
import jatx.russianrocksongbook.commonviewmodel.CommonViewModel
import jatx.russianrocksongbook.domain.repository.preferences.colorBlack

@Composable
fun ConfirmDialog(
    commonViewModel: CommonViewModel = CommonViewModel.getInstance(),
    @StringRes titleRes: Int,
    @StringRes messageRes: Int,
    invertColors: Boolean = false,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    onDecline: (() -> Unit)? = null
) {
    val theme = commonViewModel.theme.collectAsState().value
    val fontSizeTitleSp = dimensionResource(id = R.dimen.text_size_20)
        .toScaledSp(ScalePow.TEXT)

    val colorMain = if (!invertColors) theme.colorMain else theme.colorBg

    AlertDialog(
        onDismissRequest = {
            onDismiss()
        },
        backgroundColor = theme.colorCommon,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = titleRes),
                    textAlign = TextAlign.Center,
                    color = colorBlack,
                    fontWeight = FontWeight.W700,
                    fontSize = fontSizeTitleSp
                )
                Divider(
                    color = theme.colorCommon,
                    thickness = 30.dp
                )
                Text(
                    text = stringResource(id = messageRes),
                    textAlign = TextAlign.Center,
                    color = colorBlack,
                    fontWeight = FontWeight.W400,
                    fontSize = fontSizeTitleSp * 0.7f
                )
            }
        },
        buttons = {
            Button(
                modifier = Modifier
                    .background(colorMain)
                    .padding(2.dp)
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults
                    .buttonColors(
                        backgroundColor = theme.colorCommon,
                        contentColor = colorBlack
                    ),
                onClick = {
                    onDismiss.invoke()
                    onConfirm.invoke()
                }) {
                Text(text = stringResource(id = R.string.ok))
            }
            Button(
                modifier = Modifier
                    .background(colorMain)
                    .padding(2.dp)
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults
                    .buttonColors(
                        backgroundColor = theme.colorCommon,
                        contentColor = colorBlack
                    ),
                onClick = {
                    onDismiss.invoke()
                    onDecline?.invoke()
                }) {
                Text(text = stringResource(id = R.string.cancel))
            }
        }
    )
}

