package jatx.russianrocksongbook.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import jatx.russianrocksongbook.R
import jatx.russianrocksongbook.preferences.Theme
import jatx.russianrocksongbook.viewmodel.MvvmViewModel

@Composable
fun CommonPanelDivider(W: Dp, H: Dp, theme: Theme) {
    val A = if (W < H) W * 3.0f / 21 else H * 3.0f / 21
    val C = if (W < H) (W - A * 6.0f) / 5 else (H - A * 6.0f) / 5

    if (W < H) {
        Divider(
            modifier = Modifier
                .fillMaxHeight()
                .width(C)
                .background(theme.colorBg)
        )
    } else {
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .height(C)
                .background(theme.colorBg)
        )
    }
}

@Composable
fun CommonNavigationIcon(mvvmViewModel: MvvmViewModel) {
    IconButton(onClick = {
        mvvmViewModel.back { }
    }) {
        Icon(painterResource(id = R.drawable.ic_back), "")
    }
}

@Composable
fun CommonSongListStub(
    fontSizeSp: TextUnit,
    theme: Theme
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.label_placeholder),
            textAlign = TextAlign.Center,
            fontSize = fontSizeSp,
            color = theme.colorMain
        )
    }
}