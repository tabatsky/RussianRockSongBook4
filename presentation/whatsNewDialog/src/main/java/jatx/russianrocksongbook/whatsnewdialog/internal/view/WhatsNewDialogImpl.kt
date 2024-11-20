package jatx.russianrocksongbook.whatsnewdialog.internal.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import jatx.russianrocksongbook.commonview.font.toScaledSp
import jatx.russianrocksongbook.commonview.theme.LocalAppTheme
import jatx.russianrocksongbook.commonviewmodel.AppWasUpdated
import jatx.russianrocksongbook.domain.repository.preferences.ScalePow
import jatx.russianrocksongbook.commonviewmodel.CommonViewModel
import jatx.russianrocksongbook.domain.repository.preferences.colorBlack
import jatx.russianrocksongbook.whatsnewdialog.R

@Composable
internal fun WhatsNewDialogImpl(
    appWasUpdatedWhenNoViewModel: Boolean = false
) {
    val theme = LocalAppTheme.current

    val fontSizeTitleSp = dimensionResource(id = R.dimen.text_size_20)
        .toScaledSp(ScalePow.TEXT)

    val appWasUpdated = CommonViewModel.getStoredInstance()?.let {
        val appState by it.appStateFlow.collectAsState()
        appState.appWasUpdated
    } ?: appWasUpdatedWhenNoViewModel

    val onDismiss = {
        CommonViewModel.getStoredInstance()
            ?.submitAction(AppWasUpdated(false))
    }

    if (appWasUpdated) {
        Dialog(
            onDismissRequest = {
                onDismiss()
            },
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxHeight(0.8f)
                        .background(theme.colorMain)
                ) {
                    Text(
                        text = stringResource(id = R.string.whats_new_title),
                        modifier = Modifier
                            .padding(10.dp),
                        color = theme.colorBg,
                        fontWeight = FontWeight.W700,
                        fontSize = fontSizeTitleSp
                    )
                    LazyColumn(
                        modifier = Modifier
                            .weight(1.0f)
                    ) {
                        item {
                            Text(
                                text = stringResource(id = R.string.whats_new_message),
                                modifier = Modifier
                                    .padding(10.dp),
                                color = theme.colorBg,
                                fontWeight = FontWeight.W400,
                                fontSize = fontSizeTitleSp * 0.7f
                            )
                        }
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
                        Text(text = stringResource(id = R.string.ok))
                    }
                }
            }
        )
    }
}