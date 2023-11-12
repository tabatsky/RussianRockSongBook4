package jatx.russianrocksongbook.commonview.dialogs.music

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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

@Composable
internal fun MusicDialog(
    commonViewModel: CommonViewModel,
    @StringRes stringRes: Int,
    onConfirm: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    val theme = commonViewModel.theme.collectAsState().value
    val fontSizeTitleSp = dimensionResource(id = R.dimen.text_size_20)
        .toScaledSp(commonViewModel.fontScaler, ScalePow.TEXT)

    AlertDialog(
        onDismissRequest = {
            onDismiss()
        },
        backgroundColor = theme.colorMain,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = stringRes),
                    textAlign = TextAlign.Center,
                    color = theme.colorBg,
                    fontWeight = FontWeight.W700,
                    fontSize = fontSizeTitleSp
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
                    onConfirm(false)
                }) {
                Text(text = stringResource(id = R.string.yes))
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
                    onConfirm(true)
                }) {
                Text(text = stringResource(id = R.string.dont_ask_more))
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
