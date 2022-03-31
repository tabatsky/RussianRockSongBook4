package jatx.russianrocksongbook.addsongsfromdirhelper.api

import androidx.documentfile.provider.DocumentFile

interface AddSongsFromDirHelper {
    fun addSongsFromDir(
        onPickedDirReturned: (DocumentFile) -> Unit,
        onPathReturned: (String) -> Unit
    )
}