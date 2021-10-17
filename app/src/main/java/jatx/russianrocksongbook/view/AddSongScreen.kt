package jatx.russianrocksongbook.view

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import jatx.russianrocksongbook.R
import jatx.russianrocksongbook.data.ScalePow
import jatx.russianrocksongbook.viewmodel.MvvmViewModel

@Composable
fun AddSongScreen(mvvmViewModel: MvvmViewModel) {
    var artist by rememberSaveable { mutableStateOf("") }
    var title by rememberSaveable { mutableStateOf("") }
    var text by rememberSaveable { mutableStateOf("") }

    val theme = mvvmViewModel.settings.theme

    val fontScale = mvvmViewModel.settings.getSpecificFontScale(ScalePow.TEXT)
    val fontSizeTextDp = dimensionResource(id = R.dimen.text_size_16) * fontScale
    val fontSizeTextSp = with(LocalDensity.current) {
        fontSizeTextDp.toSp()
    }

    val showUploadDialog by mvvmViewModel.showUploadDialogForSong.collectAsState()
    Log.e("show upload", showUploadDialog.toString())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = theme.colorBg)
    ) {
        TopAppBar(
            title = {
                Text(text = stringResource(id = R.string.add_song))
            },
            backgroundColor = theme.colorCommon,
            navigationIcon = {
                IconButton(onClick = {
                    mvvmViewModel.back { }
                }) {
                    Icon(painterResource(id = R.drawable.ic_back), "")
                }
            }
        )
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
                    .fillMaxWidth()
                    .wrapContentHeight(),
                label = {
                    Text(
                        text = stringResource(id = R.string.artist),
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
                    .fillMaxWidth()
                    .wrapContentHeight(),
                label = {
                    Text(
                        text = stringResource(id = R.string.title),
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
                    .fillMaxWidth()
                    .weight(1.0f),
                label = {
                    Text(
                        text = stringResource(id = R.string.song_text),
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
                        mvvmViewModel.showToast(R.string.toast_fill_all_fields)
                    } else {
                        mvvmViewModel.addSongToRepo(
                            artist, title, text
                        )
                    }
                }) {
                Text(text = stringResource(id = R.string.save))
            }
        }
        if (showUploadDialog) {
            UploadDialog(
                mvvmViewModel = mvvmViewModel,
                onConfirm = {
                    mvvmViewModel.hideUploadOfferForSong()
                    mvvmViewModel.uploadNewToCloud()
                },
                onDismiss = {
                    Log.e("upload dialog", "on dismiss")
                    mvvmViewModel.hideUploadOfferForSong()
                    mvvmViewModel.showNewSong()
                }
            )
        }
    }
}