package jatx.russianrocksongbook.helpers.api

import androidx.documentfile.provider.DocumentFile

interface AddSongsFromDirHelper {
    fun addSongsFromDir(
        onPickedDirReturned: (DocumentFile) -> Unit,
        onPathReturned: (String) -> Unit
    )
}