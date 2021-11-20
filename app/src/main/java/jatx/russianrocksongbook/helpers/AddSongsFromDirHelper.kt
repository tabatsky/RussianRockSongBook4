package jatx.russianrocksongbook.helpers

import android.app.Activity
import android.content.ActivityNotFoundException
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.documentfile.provider.DocumentFile
import com.obsez.android.lib.filechooser.ChooserDialog
import javax.inject.Inject

class AddSongsFromDirHelper @Inject constructor(
    private val activity: Activity
) {
    private var onPickedDirReturned: (DocumentFile) -> Unit = {}
    private var onPathReturned: (String) -> Unit = {}

    private val openDirResultLauncher = if (activity is ComponentActivity) activity.registerForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        uri?.apply {
            val pickedDir = DocumentFile.fromTreeUri(activity, this)
            pickedDir?.apply {
                onPickedDirReturned(this)
            }
        }
    } else null

    fun addSongsFromDir(
        onPickedDirReturned: (DocumentFile) -> Unit,
        onPathReturned: (String) -> Unit
    ) {
        this.onPickedDirReturned = onPickedDirReturned
        this.onPathReturned = onPathReturned

        try {
            if (Build.VERSION.SDK_INT < 29) {
                showFileSelectDialog()
            } else {
                openDirResultLauncher?.launch(Uri.parse(DocumentsContract.EXTRA_INITIAL_URI))
            }
        } catch (e: ActivityNotFoundException) {
            showFileSelectDialog()
        }
    }

    private fun showFileSelectDialog() {
        ChooserDialog(activity)
            .withFilter(true, false)
            .withStartFile(activity.getExternalFilesDir(null)?.absolutePath)
            .withChosenListener { path, _ ->
                onPathReturned(path)
            }
            .withOnCancelListener { dialog ->
                dialog.cancel()
            }
            .build()
            .show()
    }
}