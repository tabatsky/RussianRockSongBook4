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
import jatx.russianrocksongbook.addartist.R
import jatx.russianrocksongbook.addartist.internal.viewmodel.AddArtistViewModel
import jatx.russianrocksongbook.addartist.internal.viewmodel.HideUploadOfferForDir
import jatx.russianrocksongbook.addartist.internal.viewmodel.ShowNewArtist
import jatx.russianrocksongbook.addartist.internal.viewmodel.UploadListToCloud
import jatx.russianrocksongbook.commonview.appbar.CommonSideAppBar
import jatx.russianrocksongbook.commonview.appbar.CommonTopAppBar
import jatx.russianrocksongbook.commonview.dialogs.confirm.UploadDialog

@Composable
internal fun AddArtistScreenImpl() {
    val addArtistViewModel = AddArtistViewModel.getInstance()

    val addArtistState by addArtistViewModel.addArtistState.collectAsState()

    val showUploadDialog = addArtistState.showUploadDialogForDir
    val newArtist = addArtistState.newArtist

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
                    addArtistViewModel.submitAction(HideUploadOfferForDir)
                    addArtistViewModel.submitAction(UploadListToCloud)
                },
                onDismiss = {
                    addArtistViewModel.submitAction(HideUploadOfferForDir)
                    addArtistViewModel.submitAction(ShowNewArtist(newArtist))
                }
            )
        }
    }
}
