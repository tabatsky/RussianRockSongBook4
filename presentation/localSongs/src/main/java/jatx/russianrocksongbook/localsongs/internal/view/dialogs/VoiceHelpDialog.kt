package jatx.russianrocksongbook.localsongs.internal.view.dialogs

import androidx.compose.foundation.background
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import jatx.russianrocksongbook.commonview.font.toScaledSp
import jatx.russianrocksongbook.commonview.theme.LocalAppTheme
import jatx.russianrocksongbook.domain.repository.preferences.ScalePow
import jatx.russianrocksongbook.domain.repository.preferences.colorBlack
import jatx.russianrocksongbook.localsongs.R

@OptIn(ExperimentalLayoutApi::class)
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
            FlowRow(
                modifier = Modifier
                    .background(theme.colorCommon)
                    .fillMaxWidth()
                    .wrapContentHeight(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(dimensionResource(R.dimen.padding_20))
                        .clickable {
                            onDismiss()
                            onConfirm(false)
                        },
                    color = colorBlack,
                    fontWeight = FontWeight.W500,
                    text = stringResource(id = R.string.accept)
                )
                Text(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(dimensionResource(R.dimen.padding_20))
                        .clickable {
                            onDismiss()
                        },
                    color = colorBlack,
                    fontWeight = FontWeight.W500,
                    text = stringResource(id = R.string.cancel)
                )
                Text(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(dimensionResource(R.dimen.padding_20))
                        .clickable {
                            onDismiss()
                            onConfirm(true)
                        },
                    color = colorBlack,
                    fontWeight = FontWeight.W500,
                    text = stringResource(id = R.string.dont_ask_more)
                )
            }
        }
    )
}