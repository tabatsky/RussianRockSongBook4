package jatx.russianrocksongbook.start.internal.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import jatx.russianrocksongbook.commonview.font.toScaledSp
import jatx.russianrocksongbook.domain.repository.preferences.ScalePow
import jatx.russianrocksongbook.start.R
import jatx.russianrocksongbook.start.internal.viewmodel.AsyncInit
import jatx.russianrocksongbook.start.internal.viewmodel.StartViewModel

@Composable
internal fun StartScreenImpl() {
    val startViewModel = StartViewModel.getInstance()

    LaunchedEffect(Unit) {
        startViewModel.submitAction(AsyncInit)
    }

    val theme = startViewModel.theme.collectAsState().value

    val startState by startViewModel.startState.collectAsState()

    val currentProgress = startState.stubCurrentProgress
    val totalProgress = startState.stubTotalProgress
    val progress = 1.0f * currentProgress / totalProgress

    val fontSizeLabel1Sp = dimensionResource(id = R.dimen.text_size_20)
        .toScaledSp(startViewModel.fontScaler, ScalePow.TEXT)
    val fontSizeLabel2Sp = dimensionResource(id = R.dimen.text_size_12)
        .toScaledSp(startViewModel.fontScaler, ScalePow.TEXT)
    val fontSizeProgressSp = dimensionResource(id = R.dimen.text_size_20)
        .toScaledSp(startViewModel.fontScaler, ScalePow.TEXT)

    Column(
        modifier = Modifier
            .background(theme.colorBg)
            .padding(16.dp)
            .fillMaxWidth()
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(id = R.string.wait_please),
            modifier = Modifier
                .padding(dimensionResource(id = R.dimen.song_text_empty)),
            color = theme.colorMain,
            fontSize = fontSizeLabel1Sp
        )
        if (startViewModel.needShowStartScreen) {
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .height(20.dp)
                    .background(theme.colorBg),
                color = theme.colorMain
            )
            Text(
                text = "$currentProgress из $totalProgress",
                color = theme.colorMain,
                fontSize = fontSizeProgressSp
            )
            Text(
                text = stringResource(id = R.string.wait_db_init),
                color = theme.colorMain,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.song_text_empty)),
                fontSize = fontSizeLabel2Sp
            )
        }
    }
}