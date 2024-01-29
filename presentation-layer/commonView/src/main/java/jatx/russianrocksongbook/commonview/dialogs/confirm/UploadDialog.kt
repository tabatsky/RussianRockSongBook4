package jatx.russianrocksongbook.commonview.dialogs.confirm

import androidx.compose.runtime.Composable
import jatx.russianrocksongbook.commonview.R

@Composable
fun UploadDialog(
    invertColors: Boolean = false,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    onDecline: (() -> Unit)? = null
) = ConfirmDialog(
    invertColors =  invertColors,
    titleRes = R.string.dialog_upload_to_cloud_title,
    messageRes = R.string.dialog_upload_to_cloud_message,
    onConfirm = onConfirm,
    onDismiss = onDismiss,
    onDecline = onDecline
)