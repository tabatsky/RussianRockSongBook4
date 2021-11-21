package jatx.russianrocksongbook.view

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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import jatx.russianrocksongbook.R
import jatx.russianrocksongbook.model.preferences.ScalePow
import jatx.russianrocksongbook.viewmodel.MvvmViewModel


@Composable
fun WhatsNewDialog(
    mvvmViewModel: MvvmViewModel = viewModel()
) {
    val theme = mvvmViewModel.settings.theme
    val fontScale = mvvmViewModel.settings.getSpecificFontScale(ScalePow.TEXT)
    val fontSizeTitleDp = dimensionResource(id = R.dimen.text_size_20) * fontScale
    val fontSizeTitleSp = with(LocalDensity.current) {
        fontSizeTitleDp.toSp()
    }

    val appWasUpdated by mvvmViewModel.appWasUpdated.collectAsState()
    val onDismiss = {
        mvvmViewModel.setAppWasUpdated(false)
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
                        Text(text = stringResource(id = R.string.ok))
                    }
                }

            }
        )
    }
}