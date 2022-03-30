package jatx.russianrocksongbook.filesystem.data.repository.adapters

import androidx.documentfile.provider.DocumentFile
import jatx.russianrocksongbook.domain.models.Song
import java.io.File

interface FileSystemAdapter {
    fun getSongsFromDir(
        pickedDir: DocumentFile,
        onFileNotFound: (String) -> Unit
    ) : Pair<String, List<Song>>

    fun getSongsFromDir(
        dir: File,
        onFileNotFound: (String) -> Unit
    ): Pair<String, List<Song>>
}