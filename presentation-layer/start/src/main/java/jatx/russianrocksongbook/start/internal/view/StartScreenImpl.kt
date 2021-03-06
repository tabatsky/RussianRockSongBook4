package jatx.russianrocksongbook.start.internal.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import jatx.russianrocksongbook.domain.repository.preferences.ScalePow
import jatx.russianrocksongbook.start.R
import jatx.russianrocksongbook.start.internal.viewmodel.StartViewModel

@Composable
internal fun StartScreenImpl() {
    val startViewModel: StartViewModel = viewModel()

    val theme = startViewModel.settings.theme
    val currentProgress by startViewModel.stubCurrentProgress.collectAsState()
    val totalProgress by startViewModel.stubTotalProgress.collectAsState()
    val progress = 1.0f * currentProgress / totalProgress

    val fontScale = startViewModel.settings.getSpecificFontScale(ScalePow.TEXT)

    val fontSizeLabel1Dp = dimensionResource(id = R.dimen.text_size_20) * fontScale
    val fontSizeLabel1Sp = with(LocalDensity.current) {
        fontSizeLabel1Dp.toSp()
    }

    val fontSizeLabel2Dp = dimensionResource(id = R.dimen.text_size_12) * fontScale
    val fontSizeLabel2Sp = with(LocalDensity.current) {
        fontSizeLabel2Dp.toSp()
    }

    val fontSizeProgressDp = dimensionResource(id = R.dimen.text_size_20) * fontScale
    val fontSizeProgressSp = with(LocalDensity.current) {
        fontSizeProgressDp.toSp()
    }

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
        LinearProgressIndicator(
            progress= progress,
            modifier = Modifier
                .height(20.dp)
                .background(theme.colorBg),
            color = theme.colorMain
        )
        Text(
            text = "$currentProgress ???? $totalProgress",
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