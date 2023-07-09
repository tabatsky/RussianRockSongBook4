package jatx.russianrocksongbook.addartist.api.methods

import androidx.documentfile.provider.DocumentFile
import jatx.russianrocksongbook.addartist.internal.viewmodel.AddArtistViewModel

fun copySongsFromDirToRepoWithPath(path: String) {
    AddArtistViewModel
        .getStoredInstance()
        ?.copySongsFromDirToRepoWithPath(path)
}

fun copySongsFromDirToRepoWithPickedDir(pickedDir: DocumentFile) {
    AddArtistViewModel
        .getStoredInstance()
        ?.copySongsFromDirToRepoWithPickedDir(pickedDir)
}