package jatx.russianrocksongbook.addartist.api.ext

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.documentfile.provider.DocumentFile
import jatx.russianrocksongbook.addartist.internal.viewmodel.AddArtistViewModel

fun ComponentActivity.copySongsFromDirToRepoWithPath(path: String) {
    val addArtistViewModel: AddArtistViewModel by viewModels()
    addArtistViewModel.copySongsFromDirToRepoWithPath(path)
}

fun ComponentActivity.copySongsFromDirToRepoWithPickedDir(pickedDir: DocumentFile) {
    val addArtistViewModel: AddArtistViewModel by viewModels()
    addArtistViewModel.copySongsFromDirToRepoWithPickedDir(pickedDir)
}