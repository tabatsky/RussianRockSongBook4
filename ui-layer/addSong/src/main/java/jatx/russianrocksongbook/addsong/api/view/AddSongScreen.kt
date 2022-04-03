package jatx.russianrocksongbook.addsong.api.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import jatx.russianrocksongbook.addsong.R
import jatx.russianrocksongbook.addsong.internal.viewmodel.AddSongViewModel
import jatx.russianrocksongbook.commonview.appbar.CommonSideAppBar
import jatx.russianrocksongbook.commonview.appbar.CommonTopAppBar
import jatx.russianrocksongbook.commonview.dialogs.confirm.UploadDialog
import jatx.russianrocksongbook.domain.repository.preferences.ScalePow
import jatx.russianrocksongbook.testing.TEXT_FIELD_ARTIST
import jatx.russianrocksongbook.testing.TEXT_FIELD_TEXT
import jatx.russianrocksongbook.testing.TEXT_FIELD_TITLE

@Composable
fun AddSongScreen() {
    val addSongViewModel: AddSongViewModel = viewModel()

    val showUploadDialog by addSongViewModel.showUploadDialogForSong.collectAsState()

    val theme = addSongViewModel.settings.theme

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val W = this.maxWidth
        val H = this.maxHeight

        if (W < H) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = theme.colorBg)
            ) {
                CommonTopAppBar(title = stringResource(id = R.string.title_add_song))
                AddSongBody()
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = theme.colorBg)
            ) {
                CommonSideAppBar(title = stringResource(id = R.string.title_add_song))
                AddSongBody()
            }
        }

        if (showUploadDialog) {
            UploadDialog(
                invertColors = true,
                onConfirm = {
                    addSongViewModel.hideUploadOfferForSong()
                    addSongViewModel.uploadNewToCloud()
                },
                onDismiss = {
                    addSongViewModel.hideUploadOfferForSong()
                    addSongViewModel.showNewSong()
                }
            )
        }
    }
}

@Composable
private fun AddSongBody() {
    val addSongViewModel: AddSongViewModel = viewModel()

    var artist by rememberSaveable { mutableStateOf("") }
    var title by rememberSaveable { mutableStateOf("") }
    var text by rememberSaveable { mutableStateOf("") }

    val theme = addSongViewModel.settings.theme

    val fontScale = addSongViewModel.settings.getSpecificFontScale(ScalePow.TEXT)
    val fontSizeTextDp = dimensionResource(id = R.dimen.text_size_16) * fontScale
    val fontSizeTextSp = with(LocalDensity.current) {
        fontSizeTextDp.toSp()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
    ) {
        TextField(
            value = artist,
            onValueChange = {
                artist = it
            },
            modifier = Modifier
                .testTag(TEXT_FIELD_ARTIST)
                .fillMaxWidth()
                .wrapContentHeight(),
            label = {
                Text(
                    text = stringResource(id = R.string.field_artist),
                    color = theme.colorBg,
                    fontSize = fontSizeTextSp * 0.7f
                )
            },
            colors = TextFieldDefaults
                .textFieldColors(
                    backgroundColor = theme.colorMain,
                    textColor = theme.colorBg
                ),
            textStyle = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize = fontSizeTextSp
            )
        )
        Divider(
            color = theme.colorBg,
            thickness = 4.dp
        )
        TextField(
            value = title,
            onValueChange = {
                title = it
            },
            modifier = Modifier
                .testTag(TEXT_FIELD_TITLE)
                .fillMaxWidth()
                .wrapContentHeight(),
            label = {
                Text(
                    text = stringResource(id = R.string.field_title),
                    color = theme.colorBg,
                    fontSize = fontSizeTextSp * 0.7f
                )
            },
            colors = TextFieldDefaults
                .textFieldColors(
                    backgroundColor = theme.colorMain,
                    textColor = theme.colorBg
                ),
            textStyle = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize = fontSizeTextSp
            )
        )
        Divider(
            color = theme.colorBg,
            thickness = 4.dp
        )
        TextField(
            value = text,
            onValueChange = {
                text = it
            },
            modifier = Modifier
                .testTag(TEXT_FIELD_TEXT)
                .fillMaxWidth()
                .weight(1.0f),
            label = {
                Text(
                    text = stringResource(id = R.string.field_song_text),
                    color = theme.colorBg,
                    fontSize = fontSizeTextSp * 0.7f
                )
            },
            colors = TextFieldDefaults
                .textFieldColors(
                    backgroundColor = theme.colorMain,
                    textColor = theme.colorBg
                ),
            textStyle = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize = fontSizeTextSp
            )
        )
        Divider(
            color = theme.colorBg,
            thickness = 4.dp
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
                if (
                    artist.trim().isEmpty() ||
                    title.trim().isEmpty() ||
                    text.trim().isEmpty()
                ) {
                    addSongViewModel.showToast(R.string.toast_fill_all_fields)
                } else {
                    addSongViewModel.addSongToRepo(
                        artist, title, text
                    )
                }
            }) {
            Text(text = stringResource(id = R.string.save))
        }
    }
}