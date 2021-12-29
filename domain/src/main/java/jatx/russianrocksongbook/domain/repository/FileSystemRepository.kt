package jatx.russianrocksongbook.domain.repository

import androidx.documentfile.provider.DocumentFile
import jatx.russianrocksongbook.domain.models.Song
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