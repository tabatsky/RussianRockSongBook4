package jatx.russianrocksongbook.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import jatx.russianrocksongbook.R
import jatx.russianrocksongbook.model.preferences.ScalePow
import jatx.russianrocksongbook.viewmodel.CurrentScreenVariant
import jatx.russianrocksongbook.viewmodel.MvvmViewModel

@Composable
fun AddArtistScreen(mvvmViewModel: MvvmViewModel = viewModel()) {
    val showUploadDialog by mvvmViewModel.showUploadDialogForDir.collectAsState()
    val uploadArtist by mvvmViewModel.uploadArtist.collectAsState()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val W = this.maxWidth
        val H = this.maxHeight

        if (W < H) {
            Column {
                CommonTopAppBar(title = stringResource(id = R.string.add_artist))
                AddArtistBody()
            }
        } else {
            Row {
                CommonSideAppBar(title = stringResource(id = R.string.add_artist))
                AddArtistBody()
            }
        }

        if (showUploadDialog) {
            UploadDialog(
                onConfirm = {
                    mvvmViewModel.hideUploadOfferForDir()
                    mvvmViewModel.uploadListToCloud()
                },
                onDismiss = {
                    mvvmViewModel.hideUploadOfferForDir()
                    mvvmViewModel.selectArtist(uploadArtist) {}
                    mvvmViewModel.selectScreen(CurrentScreenVariant.SONG_LIST)
                }
            )
        }
    }
}

@Composable
private fun AddArtistBody(
    mvvmViewModel: MvvmViewModel = viewModel()
) {
    val theme = mvvmViewModel.settings.theme

    val fontScale = mvvmViewModel.settings.getSpecificFontScale(ScalePow.TEXT)
    val fontSizeTextDp = dimensionResource(id = R.dimen.text_size_16) * fontScale
    val fontSizeTextSp = with(LocalDensity.current) {
        fontSizeTextDp.toSp()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = theme.colorBg)
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
    }
}