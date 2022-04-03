package jatx.russianrocksongbook.cloudsongs.internal.view.cloudsongtext

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import jatx.russianrocksongbook.domain.repository.preferences.Theme

@Composable
internal fun CloudSongTextProgress(
    theme: Theme
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.colorBg),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .width(100.dp)
                .height(100.dp)
                .background(theme.colorBg),
            color = theme.colorMain
        )
    }
}