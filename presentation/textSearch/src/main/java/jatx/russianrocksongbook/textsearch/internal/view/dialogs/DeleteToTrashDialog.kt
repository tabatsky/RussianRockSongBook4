package jatx.russianrocksongbook.textsearch.internal.view.dialogs

import androidx.compose.runtime.Composable
import jatx.russianrocksongbook.commonview.dialogs.confirm.ConfirmDialog
import jatx.russianrocksongbook.textsearch.R
import jatx.russianrocksongbook.textsearch.internal.viewmodel.DeleteCurrentToTrash
import jatx.russianrocksongbook.textsearch.internal.viewmodel.TextSearchViewModel

@Composable
internal fun DeleteToTrashDialog(
    onDismiss: () -> Unit
) {
    val textSearchViewModel = TextSearchViewModel.getInstance()

    ConfirmDialog(
        titleRes = R.string.dialog_song_to_trash_title,
        messageRes = R.string.dialog_song_to_trash_message,
        onConfirm = {
            textSearchViewModel.submitAction(DeleteCurrentToTrash)
        },
        onDismiss = onDismiss
    )
}
