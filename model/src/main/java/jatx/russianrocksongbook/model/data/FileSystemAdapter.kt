package jatx.russianrocksongbook.model.data

import androidx.documentfile.provider.DocumentFile
import jatx.russianrocksongbook.model.domain.Song
import java.io.File

interface FileSystemAdapter{
    fun getSongsFromDir(
        pickedDir: DocumentFile,
        onFileNotFound: (String) -> Unit
    ) : Pair<String, List<Song>>

    fun getSongsFromDir(
        dir: File,
        onFileNotFound: (String) -> Unit
    ): Pair<String, List<Song>>
}