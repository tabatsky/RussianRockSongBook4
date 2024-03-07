package jatx.russianrocksongbook.domain.repository.filesystem

import androidx.documentfile.provider.DocumentFile
import jatx.russianrocksongbook.domain.models.local.Song
import java.io.File

interface FileSystemRepository{
    fun getSongsFromDir(
        pickedDir: DocumentFile,
        onFileNotFound: (String) -> Unit
    ) : Pair<String, List<Song>>

    fun getSongsFromDir(
        dir: File,
        onFileNotFound: (String) -> Unit
    ): Pair<String, List<Song>>
}