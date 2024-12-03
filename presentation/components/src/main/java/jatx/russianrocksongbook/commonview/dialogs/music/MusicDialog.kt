package jatx.russianrocksongbook.commonview.dialogs.music

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import jatx.russianrocksongbook.commonview.R
import jatx.russianrocksongbook.commonview.font.toScaledSp
import jatx.russianrocksongbook.commonview.theme.LocalAppTheme
import jatx.russianrocksongbook.domain.repository.preferences.ScalePow
import jatx.russianrocksongbook.domain.repository.preferences.colorBlack

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun MusicDialog(
    @StringRes stringRes: Int,
    onConfirm: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    val theme = LocalAppTheme.current
    val fontSizeTitleSp = dimensionResource(id = R.dimen.text_size_20)
        .toScaledSp(ScalePow.TEXT)

    AlertDialog(
        onDismissRequest = {
            onDismiss()
        },
        backgroundColor = theme.colorCommon,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = stringRes),
                    textAlign = TextAlign.Center,
                    color = colorBlack,
                    fontWeight = FontWeight.W700,
                    fontSize = fontSizeTitleSp
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
                            onDismiss()
                            onConfirm(false)
                        },
                    color = colorBlack,
                    fontWeight = FontWeight.W500,
                    text = stringResource(id = R.string.yes)
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
