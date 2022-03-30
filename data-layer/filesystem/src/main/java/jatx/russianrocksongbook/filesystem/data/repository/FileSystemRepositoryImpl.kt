package jatx.russianrocksongbook.filesystem.data.repository

import android.content.Context
import androidx.documentfile.provider.DocumentFile
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import it.czerwinski.android.hilt.annotations.BoundTo
import jatx.russianrocksongbook.domain.models.Song
import jatx.russianrocksongbook.domain.repository.FileSystemRepository
import jatx.russianrocksongbook.filesystem.data.repository.adapters.FileSystemAdapter
import java.io.File
import java.io.FileNotFoundException
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@BoundTo(supertype = FileSystemRepository::class, component = SingletonComponent::class)
class FileSystemRepositoryImpl @Inject constructor(
    private val adapter: FileSystemAdapter
): FileSystemRepository {
    override fun getSongsFromDir(
        pickedDir: DocumentFile,
        onFileNotFound: (String) -> Unit
    ) = adapter.getSongsFromDir(pickedDir, onFileNotFound)

    override fun getSongsFromDir(
        dir: File,
        onFileNotFound: (String) -> Unit
    ) = adapter.getSongsFromDir(dir, onFileNotFound)

}