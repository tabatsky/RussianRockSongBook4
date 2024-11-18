package jatx.russianrocksongbook.commonview.dialogs.delete

import androidx.compose.runtime.Composable
import jatx.russianrocksongbook.commonview.R
import jatx.russianrocksongbook.commonview.dialogs.confirm.ConfirmDialog
import jatx.russianrocksongbook.commonviewmodel.DeleteCurrentToTrash
import jatx.russianrocksongbook.commonviewmodel.UIAction

@Composable
fun DeleteToTrashDialog(
    onDismiss: () -> Unit,
    submitAction: (UIAction) -> Unit
) {
    ConfirmDialog(
        titleRes = R.string.dialog_song_to_trash_title,
        messageRes = R.string.dialog_song_to_trash_message,
        onConfirm = {
            submitAction(DeleteCurrentToTrash)
        },
        onDismiss = onDismiss
    )
}