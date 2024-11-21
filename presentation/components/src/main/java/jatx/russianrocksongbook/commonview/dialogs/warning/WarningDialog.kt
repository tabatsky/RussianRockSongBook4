package jatx.russianrocksongbook.commonview.dialogs.warning

import androidx.compose.runtime.*
import jatx.russianrocksongbook.commonviewmodel.CommonViewModel

@Composable
fun WarningDialog(
    commonViewModel: CommonViewModel = CommonViewModel.getInstance(),
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    WarningDialogContent(
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        submitEffect = commonViewModel::submitEffect
    )
}
