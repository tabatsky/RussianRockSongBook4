package jatx.russianrocksongbook.commonview.dialogs.confirm

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import jatx.russianrocksongbook.commonview.R
import jatx.russianrocksongbook.commonview.font.toScaledSp
import jatx.russianrocksongbook.commonview.theme.LocalAppTheme
import jatx.russianrocksongbook.domain.repository.preferences.ScalePow
import jatx.russianrocksongbook.domain.repository.preferences.colorBlack

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ConfirmDialog(
    @StringRes titleRes: Int,
    @StringRes messageRes: Int,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    onDecline: (() -> Unit)? = null
) {
    val theme = LocalAppTheme.current

    val fontSizeTitleSp = dimensionResource(id = R.dimen.text_size_20)
        .toScaledSp(ScalePow.TEXT)
    val fontSizeMessageSp = fontSizeTitleSp * 0.7f
    val fontSizeButtonSp = dimensionResource(R.dimen.text_size_16)
        .toScaledSp(ScalePow.BUTTON)

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
                    fontSize = fontSizeMessageSp
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
                            onDismiss.invoke()
                            onConfirm.invoke()
                        },
                    color = colorBlack,
                    fontWeight = FontWeight.W500,
                    fontSize = fontSizeButtonSp,
                    text = stringResource(id = R.string.ok)
                )
                Text(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(dimensionResource(R.dimen.padding_20))
                        .clickable {
                            onDismiss.invoke()
                            onDecline?.invoke()
                        },
                    color = colorBlack,
                    fontWeight = FontWeight.W500,
                    fontSize = fontSizeButtonSp,
                    text = stringResource(id = R.string.cancel)
                )
            }
        }
    )
}

