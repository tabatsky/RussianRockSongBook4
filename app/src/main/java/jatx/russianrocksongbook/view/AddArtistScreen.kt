package jatx.russianrocksongbook.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import jatx.russianrocksongbook.R
import jatx.russianrocksongbook.preferences.ScalePow
import jatx.russianrocksongbook.viewmodel.CurrentScreen
import jatx.russianrocksongbook.viewmodel.MvvmViewModel

@Composable
fun AddArtistScreen(mvvmViewModel: MvvmViewModel) {
    val theme = mvvmViewModel.settings.theme

    val fontScale = mvvmViewModel.settings.getSpecificFontScale(ScalePow.TEXT)
    val fontSizeTextDp = dimensionResource(id = R.dimen.text_size_16) * fontScale
    val fontSizeTextSp = with(LocalDensity.current) {
        fontSizeTextDp.toSp()
    }

    val showUploadDialog by mvvmViewModel.showUploadDialogForDir.collectAsState()
    val uploadArtist by mvvmViewModel.uploadArtist.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = theme.colorBg)
    ) {
        TopAppBar(
            title = {
                Text(text = stringResource(id = R.string.add_artist))
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
            LazyColumn(
                modifier = Modifier
                    .weight(1.0f)
            ) {
                item {
                    Text(
                        text = stringResource(id = R.string.add_artist_manual),
                        color = theme.colorMain,
                        fontSize = fontSizeTextSp
                    )
                }
            }
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
                    mvvmViewModel.addSongsFromDir()
                }) {
                Text(text = stringResource(id = R.string.choose))
            }
            if (showUploadDialog) {
                UploadDialog(
                    mvvmViewModel = mvvmViewModel,
                    onConfirm = {
                        mvvmViewModel.hideUploadOfferForDir()
                        mvvmViewModel.uploadListToCloud()
                    },
                    onDismiss = {
                        mvvmViewModel.hideUploadOfferForDir()
                        mvvmViewModel.selectArtist(uploadArtist) {}
                        mvvmViewModel.selectScreen(CurrentScreen.SONG_LIST)
                    }
                )
            }
        }
    }
}