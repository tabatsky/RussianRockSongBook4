package jatx.russianrocksongbook.addsongsfromdirhelper.internal.impl

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
import jatx.russianrocksongbook.addsongsfromdirhelper.api.AddSongsFromDirHelper
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
            uri?.let {
                val pickedDir = DocumentFile.fromTreeUri(activity, it)
                pickedDir?.let {
                    onPickedDirReturned(it)
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
                        activity.getExternalFilesDir(null)
                    ))
            }
        } catch (e: ActivityNotFoundException) {
            showFileSelectDialog()
        }
    }

    @Suppress("DEPRECATION")
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