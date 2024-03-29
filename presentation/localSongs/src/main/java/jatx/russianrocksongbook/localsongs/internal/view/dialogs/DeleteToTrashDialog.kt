package jatx.russianrocksongbook.localsongs.internal.view.dialogs

import androidx.compose.runtime.Composable
import jatx.russianrocksongbook.commonview.dialogs.confirm.ConfirmDialog
import jatx.russianrocksongbook.localsongs.R
import jatx.russianrocksongbook.localsongs.internal.viewmodel.DeleteCurrentToTrash
import jatx.russianrocksongbook.localsongs.internal.viewmodel.LocalViewModel

@Composable
internal fun DeleteToTrashDialog(
    onDismiss: () -> Unit
) {
    val localViewModel = LocalViewModel.getInstance()

    ConfirmDialog(
        titleRes = R.string.dialog_song_to_trash_title,
        messageRes = R.string.dialog_song_to_trash_message,
        onConfirm = {
            localViewModel.submitAction(DeleteCurrentToTrash)
        },
        onDismiss = onDismiss
    )
}
