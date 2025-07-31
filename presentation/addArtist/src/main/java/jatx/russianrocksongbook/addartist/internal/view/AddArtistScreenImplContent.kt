package jatx.russianrocksongbook.addartist.internal.view

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import jatx.russianrocksongbook.addartist.R
import jatx.russianrocksongbook.addartist.internal.viewmodel.AddArtistState
import jatx.russianrocksongbook.addartist.internal.viewmodel.HideUploadOfferForDir
import jatx.russianrocksongbook.addartist.internal.viewmodel.ShowNewArtist
import jatx.russianrocksongbook.addartist.internal.viewmodel.UploadListToCloud
import jatx.russianrocksongbook.commonview.appbar.CommonSideAppBar
import jatx.russianrocksongbook.commonview.appbar.CommonTopAppBar
import jatx.russianrocksongbook.commonview.dialogs.confirm.UploadDialog
import jatx.russianrocksongbook.commonviewmodel.UIAction
import jatx.russianrocksongbook.testing.APP_BAR_TITLE

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun AddArtistScreenImplContent(
    addArtistStateState: State<AddArtistState>,
    submitAction: (UIAction) -> Unit
) {
    val addArtistState by addArtistStateState

    val showUploadDialog = addArtistState.showUploadDialogForDir
    val newArtist = addArtistState.newArtist

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val configuration = LocalConfiguration.current
        val W = configuration.screenWidthDp.dp
        val H = configuration.screenHeightDp.dp

        if (W < H) {
            Column {
                CommonTopAppBar(
                    title = stringResource(id = R.string.title_add_artist),
                    titleTestTag = APP_BAR_TITLE
                )
                AddArtistBody(
                    submitAction = submitAction
                )
            }
        } else {
            Row {
                CommonSideAppBar(
                    title = stringResource(id = R.string.title_add_artist),
                    titleTestTag = APP_BAR_TITLE
                )
                AddArtistBody(
                    submitAction = submitAction
                )
            }
        }

        if (showUploadDialog) {
            UploadDialog(
                onConfirm = {
                    submitAction(UploadListToCloud)
                },
                onDecline = {
                    submitAction(ShowNewArtist(newArtist))
                },
                onDismiss = {
                    submitAction(HideUploadOfferForDir)
                }
            )
        }
    }
}