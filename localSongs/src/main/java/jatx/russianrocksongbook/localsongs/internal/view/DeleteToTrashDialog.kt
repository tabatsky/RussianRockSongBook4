package jatx.russianrocksongbook.localsongs.api.view

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import jatx.russianrocksongbook.commonview.ConfirmDialog
import jatx.russianrocksongbook.localsongs.R
import jatx.russianrocksongbook.localsongs.internal.viewmodel.LocalViewModel

@Composable
internal fun DeleteToTrashDialog(
    onDismiss: () -> Unit
) {
    val localViewModel: LocalViewModel = viewModel()

    ConfirmDialog(
        titleRes = R.string.dialog_song_to_trash_title,
        messageRes = R.string.dialog_song_to_trash_message,
        onConfirm = {
            localViewModel.deleteCurrentToTrash()
        },
        onDismiss = onDismiss
    )
}
