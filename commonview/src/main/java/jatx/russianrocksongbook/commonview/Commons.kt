package jatx.russianrocksongbook.commonview

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import jatx.russianrocksongbook.domain.repository.Theme
import jatx.russianrocksongbook.domain.repository.colorDarkYellow
import jatx.russianrocksongbook.testing.BACK_BUTTON
import jatx.russianrocksongbook.viewmodel.CommonViewModel
import jatx.sideappbar.SideAppBar

val COMMON_APP_BAR_WIDTH = 72.dp

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
fun CommonIconButton(
    @DrawableRes resId: Int,
    testTag: String? = null,
    onClick: () -> Unit
) {
    val modifier = testTag?.let {
        Modifier.testTag(it)
    } ?: Modifier
    IconButton(
        modifier = modifier,
        onClick = onClick
    ) {
        Icon(painterResource(id = resId), "")
    }
}

@Composable
fun CommonBackButton(
    commonViewModel: CommonViewModel = viewModel()
) {
    CommonIconButton(
        resId = R.drawable.ic_back,
        testTag = BACK_BUTTON,
        onClick = { commonViewModel.back() }
    )
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

@Composable
fun ErrorSongListStub(
    fontSizeSp: TextUnit,
    theme: Theme
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.label_error_placeholder),
            textAlign = TextAlign.Center,
            fontSize = fontSizeSp,
            color = theme.colorMain
        )
    }
}

@Composable
fun CommonTopAppBar(
    title: String? = null,
    navigationIcon: @Composable () -> Unit = { CommonBackButton() },
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = {
            title?.apply {
                Text(
                    text = this,
                    softWrap = false
                )
            }
        },
        backgroundColor = colorDarkYellow,
        navigationIcon = navigationIcon,
        actions = actions
    )
}

@Composable
fun CommonSideAppBar(
    title: String? = null,
    navigationIcon: @Composable () -> Unit = { CommonBackButton() },
    actions: @Composable ColumnScope.() -> Unit = {},
    appBarWidth: Dp = COMMON_APP_BAR_WIDTH
) {
    SideAppBar(
        title = title,
        backgroundColor = colorDarkYellow,
        navigationIcon = navigationIcon,
        actions = actions,
        appBarWidth = appBarWidth
    )
}

fun String.crop(maxLength: Int) =
    if (this.length <= maxLength)
        this
    else
        this.take(maxLength - 1) + "…"