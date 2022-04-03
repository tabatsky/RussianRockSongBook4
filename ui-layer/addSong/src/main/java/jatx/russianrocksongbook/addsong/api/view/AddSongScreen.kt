package jatx.russianrocksongbook.addsong.api.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import jatx.russianrocksongbook.addsong.R
import jatx.russianrocksongbook.addsong.internal.view.AddSongBody
import jatx.russianrocksongbook.addsong.internal.viewmodel.AddSongViewModel
import jatx.russianrocksongbook.commonview.appbar.CommonSideAppBar
import jatx.russianrocksongbook.commonview.appbar.CommonTopAppBar
import jatx.russianrocksongbook.commonview.dialogs.confirm.UploadDialog

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

