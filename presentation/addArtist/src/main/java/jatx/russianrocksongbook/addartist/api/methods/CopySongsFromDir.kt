package jatx.russianrocksongbook.addartist.api.methods

import androidx.documentfile.provider.DocumentFile
import jatx.russianrocksongbook.addartist.internal.viewmodel.AddArtistViewModel
import jatx.russianrocksongbook.addartist.internal.viewmodel.CopySongsFromDirToRepoWithPath
import jatx.russianrocksongbook.addartist.internal.viewmodel.CopySongsFromDirToRepoWithPickedDir

fun copySongsFromDirToRepoWithPath(path: String) {
    AddArtistViewModel
        .getStoredInstance()
        ?.submitAction(
            CopySongsFromDirToRepoWithPath(path)
        )
}

fun copySongsFromDirToRepoWithPickedDir(pickedDir: DocumentFile) {
    AddArtistViewModel
        .getStoredInstance()
        ?.submitAction(
            CopySongsFromDirToRepoWithPickedDir(pickedDir)
        )
}