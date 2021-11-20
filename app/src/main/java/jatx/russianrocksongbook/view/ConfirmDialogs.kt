package jatx.russianrocksongbook.view

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
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
import jatx.russianrocksongbook.R
import jatx.russianrocksongbook.preferences.ScalePow
import jatx.russianrocksongbook.viewmodel.MvvmViewModel

@Composable
fun ConfirmDialog(
    mvvmViewModel: MvvmViewModel,
    @StringRes titleRes: Int,
    @StringRes messageRes: Int,
    invertColors: Boolean = false,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val theme = mvvmViewModel.settings.theme
    val fontScale = mvvmViewModel.settings.getSpecificFontScale(ScalePow.TEXT)
    val fontSizeTitleDp = dimensionResource(id = R.dimen.text_size_20) * fontScale
    val fontSizeTitleSp = with(LocalDensity.current) {
        fontSizeTitleDp.toSp()
    }

    val colorBg = if (!invertColors) theme.colorBg else theme.colorMain
    val colorMain = if (!invertColors) theme.colorMain else theme.colorBg

    AlertDialog(
        onDismissRequest = {
            onDismiss()
        },
        backgroundColor = colorMain,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = titleRes),
                    textAlign = TextAlign.Center,
                    color = colorBg,
                    fontWeight = FontWeight.W700,
                    fontSize = fontSizeTitleSp
                )
                Divider(
                    color = colorMain,
                    thickness = 30.dp
                )
                Text(
                    text = stringResource(id = messageRes),
                    textAlign = TextAlign.Center,
                    color = colorBg,
                    fontWeight = FontWeight.W400,
                    fontSize = fontSizeTitleSp * 0.7f
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
                        contentColor = colorMain
                    ),
                onClick = {
                    onDismiss()
                    onConfirm()
                }) {
                Text(text = stringResource(id = R.string.ok))
            }
            Divider(
                color = colorMain,
                thickness = 2.dp
            )
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults
                    .buttonColors(
                        backgroundColor = theme.colorCommon,
                        contentColor = colorMain
                    ),
                onClick = {
                    onDismiss()
                }) {
                Text(text = stringResource(id = R.string.cancel))
            }
            Divider(
                color = colorMain,
                thickness = 2.dp
            )
        }
    )
}

@Composable
fun DeleteToTrashDialog(
    mvvmViewModel: MvvmViewModel,
    onDismiss: () -> Unit
) = ConfirmDialog(
    mvvmViewModel = mvvmViewModel,
    titleRes = R.string.dialog_song_to_trash_title,
    messageRes = R.string.dialog_song_to_trash_message,
    onConfirm = {
        mvvmViewModel.deleteCurrentToTrash()
    },
    onDismiss = onDismiss
)

@Composable
fun UploadDialog(
    mvvmViewModel: MvvmViewModel,
    invertColors: Boolean = false,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) = ConfirmDialog(
    mvvmViewModel = mvvmViewModel,
    invertColors =  invertColors,
    titleRes = R.string.dialog_upload_to_cloud_title,
    messageRes = R.string.dialog_upload_to_cloud_message,
    onConfirm = onConfirm,
    onDismiss = onDismiss
)
