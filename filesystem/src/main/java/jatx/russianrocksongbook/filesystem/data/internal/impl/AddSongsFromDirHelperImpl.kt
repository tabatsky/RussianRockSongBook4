package jatx.russianrocksongbook.filesystem.data.internal.impl

import android.app.Activity
import android.content.ActivityNotFoundException
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.documentfile.provider.DocumentFile
import com.obsez.android.lib.filechooser.ChooserDialog
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import it.czerwinski.android.hilt.annotations.BoundTo
import jatx.russianrocksongbook.filesystem.data.api.AddSongsFromDirHelper
import javax.inject.Inject

@ActivityScoped
@BoundTo(supertype = AddSongsFromDirHelper::class, component = ActivityComponent::class)
internal class AddSongsFromDirHelperImpl @Inject constructor(
    private val activity: Activity
): AddSongsFromDirHelper {
    private var onPickedDirReturned: (DocumentFile) -> Unit = {}
    private var onPathReturned: (String) -> Unit = {}

    private val openDirResultLauncher =
        (activity as? ComponentActivity)?.registerForActivityResult(
            ActivityResultContracts.OpenDocumentTree()
        ) { uri ->
            uri?.apply {
                val pickedDir = DocumentFile.fromTreeUri(activity, this)
                pickedDir?.apply {
                    onPickedDirReturned(this)
                }
            }
        }

    override fun addSongsFromDir(
        onPickedDirReturned: (DocumentFile) -> Unit,
        onPathReturned: (String) -> Unit
    ) {
        this.onPickedDirReturned = onPickedDirReturned
        this.onPathReturned = onPathReturned

        try {
            if (Build.VERSION.SDK_INT < 29) {
                showFileSelectDialog()
            } else {
                openDirResultLauncher?.launch(
                    Uri.fromFile(
                        Environment.getExternalStorageDirectory()
                    ))
            }
        } catch (e: ActivityNotFoundException) {
            showFileSelectDialog()
        }
    }

    private fun showFileSelectDialog() {
        ChooserDialog(activity)
            .withFilter(true, false)
            .withStartFile(Environment.getExternalStorageDirectory().absolutePath)
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