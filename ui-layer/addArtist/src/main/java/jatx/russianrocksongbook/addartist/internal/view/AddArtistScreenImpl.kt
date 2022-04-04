package jatx.russianrocksongbook.addartist.internal.view

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
import jatx.russianrocksongbook.addartist.R
import jatx.russianrocksongbook.addartist.internal.viewmodel.AddArtistViewModel
import jatx.russianrocksongbook.commonview.appbar.CommonSideAppBar
import jatx.russianrocksongbook.commonview.appbar.CommonTopAppBar
import jatx.russianrocksongbook.commonview.dialogs.confirm.UploadDialog
import jatx.russianrocksongbook.viewmodel.CurrentScreenVariant

@Composable
internal fun AddArtistScreenImpl() {
    val addArtistViewModel: AddArtistViewModel = viewModel()

    val showUploadDialog by addArtistViewModel.showUploadDialogForDir.collectAsState()
    val uploadArtist by addArtistViewModel.uploadArtist.collectAsState()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val W = this.maxWidth
        val H = this.maxHeight

        if (W < H) {
            Column {
                CommonTopAppBar(title = stringResource(id = R.string.title_add_artist))
                AddArtistBody()
            }
        } else {
            Row {
                CommonSideAppBar(title = stringResource(id = R.string.title_add_artist))
                AddArtistBody()
            }
        }

        if (showUploadDialog) {
            UploadDialog(
                onConfirm = {
                    addArtistViewModel.hideUploadOfferForDir()
                    addArtistViewModel.uploadListToCloud()
                },
                onDismiss = {
                    addArtistViewModel.hideUploadOfferForDir()
                    addArtistViewModel.callbacks.onArtistSelected(uploadArtist)
                    addArtistViewModel.selectScreen(CurrentScreenVariant.SONG_LIST)
                }
            )
        }
    }
}
