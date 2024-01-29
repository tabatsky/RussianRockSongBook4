package jatx.russianrocksongbook.addsong.internal.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import jatx.russianrocksongbook.addsong.R
import jatx.russianrocksongbook.addsong.internal.viewmodel.AddSongViewModel
import jatx.russianrocksongbook.addsong.internal.viewmodel.HideUploadOfferForSong
import jatx.russianrocksongbook.addsong.internal.viewmodel.Reset
import jatx.russianrocksongbook.addsong.internal.viewmodel.ShowNewSong
import jatx.russianrocksongbook.addsong.internal.viewmodel.UploadNewToCloud
import jatx.russianrocksongbook.commonview.appbar.CommonSideAppBar
import jatx.russianrocksongbook.commonview.appbar.CommonTopAppBar
import jatx.russianrocksongbook.commonview.dialogs.confirm.UploadDialog

@Composable
internal fun AddSongScreenImpl() {
    val addSongViewModel = AddSongViewModel.getInstance()

    var screenInitDone by rememberSaveable { mutableStateOf(false) }

    if (!screenInitDone) {
        LaunchedEffect(Unit) {
            screenInitDone = true
            addSongViewModel.submitAction(Reset)
        }
    }

    val addSongState by addSongViewModel.addSongState.collectAsState()
    val showUploadDialog = addSongState.showUploadDialogForSong

    val theme = addSongViewModel.theme.collectAsState().value

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
                    addSongViewModel.submitAction(UploadNewToCloud)
                },
                onDecline = {
                    addSongViewModel.submitAction(ShowNewSong)
                },
                onDismiss = {
                    addSongViewModel.submitAction(HideUploadOfferForSong)
                }
            )
        }
    }
}

