package jatx.russianrocksongbook.localsongs.view

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import jatx.russianrocksongbook.commonview.ConfirmDialog
import jatx.russianrocksongbook.localsongs.R
import jatx.russianrocksongbook.localsongs.viewmodel.LocalViewModel

@Composable
fun DeleteToTrashDialog(
    localViewModel: LocalViewModel = viewModel(),
    onDismiss: () -> Unit
) = ConfirmDialog(
    titleRes = R.string.dialog_song_to_trash_title,
    messageRes = R.string.dialog_song_to_trash_message,
    onConfirm = {
        localViewModel.deleteCurrentToTrash()
    },
    onDismiss = onDismiss
)
