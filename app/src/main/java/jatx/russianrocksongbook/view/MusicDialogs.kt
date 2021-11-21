package jatx.russianrocksongbook.view

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import jatx.russianrocksongbook.R
import jatx.russianrocksongbook.model.preferences.ScalePow
import jatx.russianrocksongbook.viewmodel.MvvmViewModel

@Composable
fun YandexMusicDialog(
    mvvmViewModel: MvvmViewModel = viewModel(),
    isCloudScreen: Boolean = false,
    onDismiss: () -> Unit
) = MusicDialog(
    stringRes = R.string.question_search_at_yandex_music,
    onConfirm = {
        if (isCloudScreen) {
            mvvmViewModel.openYandexMusicCloud(it)
        } else {
            mvvmViewModel.openYandexMusic(it)
        }
    },
    onDismiss = onDismiss
)

@Composable
fun VkMusicDialog(
    mvvmViewModel: MvvmViewModel = viewModel(),
    isCloudScreen: Boolean = false,
    onDismiss: () -> Unit
) = MusicDialog(
    mvvmViewModel = mvvmViewModel,
    stringRes = R.string.question_search_at_vk_music,
    onConfirm = {
        if (isCloudScreen) {
            mvvmViewModel.openVkMusicCloud(it)
        } else {
            mvvmViewModel.openVkMusic(it)
        }
    },
    onDismiss = onDismiss
)

@Composable
fun YoutubeMusicDialog(
    mvvmViewModel: MvvmViewModel = viewModel(),
    isCloudScreen: Boolean = false,
    onDismiss: () -> Unit
) = MusicDialog(
    mvvmViewModel = mvvmViewModel,
    stringRes = R.string.question_search_at_youtube_music,
    onConfirm = {
        if (isCloudScreen) {
            mvvmViewModel.openYoutubeMusicCloud(it)
        } else {
            mvvmViewModel.openYoutubeMusic(it)
        }
    },
    onDismiss = onDismiss
)

@Composable
private fun MusicDialog(
    mvvmViewModel: MvvmViewModel = viewModel(),
    @StringRes stringRes: Int,
    onConfirm: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    val theme = mvvmViewModel.settings.theme
    val fontScale = mvvmViewModel.settings.getSpecificFontScale(ScalePow.TEXT)
    val fontSizeTitleDp = dimensionResource(id = R.dimen.text_size_20) * fontScale
    val fontSizeTitleSp = with(LocalDensity.current) {
        fontSizeTitleDp.toSp()
    }

    AlertDialog(
        onDismissRequest = {
            onDismiss()
        },
        backgroundColor = theme.colorMain,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = stringRes),
                    textAlign = TextAlign.Center,
                    color = theme.colorBg,
                    fontWeight = FontWeight.W700,
                    fontSize = fontSizeTitleSp
                )
            }
        },
        buttons = {
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
                    onConfirm(false)
                }) {
                Text(text = stringResource(id = R.string.yes))
            }
            Divider(
                color = theme.colorMain,
                thickness = 2.dp
            )
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
                    onConfirm(true)
                }) {
                Text(text = stringResource(id = R.string.dont_ask_more))
            }
            Divider(
                color = theme.colorMain,
                thickness = 2.dp
            )
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
                Text(text = stringResource(id = R.string.cancel))
            }
            Divider(
                color = theme.colorMain,
                thickness = 2.dp
            )
        }
    )
}
