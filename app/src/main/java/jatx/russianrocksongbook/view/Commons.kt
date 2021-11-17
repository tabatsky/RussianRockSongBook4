package jatx.russianrocksongbook.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
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