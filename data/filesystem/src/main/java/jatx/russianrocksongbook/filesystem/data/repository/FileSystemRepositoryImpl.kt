package jatx.russianrocksongbook.filesystem.data.repository

import androidx.documentfile.provider.DocumentFile
import dagger.hilt.components.SingletonComponent
import it.czerwinski.android.hilt.annotations.BoundTo
import jatx.russianrocksongbook.domain.repository.filesystem.FileSystemRepository
import jatx.russianrocksongbook.filesystem.data.repository.adapters.FileSystemAdapter
import java.io.File
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