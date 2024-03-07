package jatx.russianrocksongbook.addartist.internal.viewmodel

import androidx.documentfile.provider.DocumentFile
import jatx.russianrocksongbook.commonviewmodel.UIAction

object HideUploadOfferForDir: UIAction
object UploadListToCloud: UIAction
data class ShowNewArtist(val artist: String): UIAction
object AddSongsFromDir: UIAction
data class CopySongsFromDirToRepoWithPath(val path: String): UIAction
data class CopySongsFromDirToRepoWithPickedDir(val pickedDir: DocumentFile): UIAction