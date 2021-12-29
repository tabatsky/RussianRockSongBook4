package jatx.russianrocksongbook.filesystem.data.repository

import android.content.Context
import androidx.documentfile.provider.DocumentFile
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import it.czerwinski.android.hilt.annotations.BoundTo
import jatx.russianrocksongbook.domain.models.Song
import jatx.russianrocksongbook.domain.repository.FileSystemRepository
import java.io.File
import java.io.FileNotFoundException
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@BoundTo(supertype = FileSystemRepository::class, component = SingletonComponent::class)
class FileSystemRepositoryImpl @Inject constructor(
    @ApplicationContext val context: Context
): FileSystemRepository {
    override fun getSongsFromDir(
        pickedDir: DocumentFile,
        onFileNotFound: (String) -> Unit
    ) : Pair<String, List<Song>> {
        val files = pickedDir.listFiles()
        val txtFileList = arrayListOf<DocumentFile>()
        files.forEach {file ->
            if (file.exists() && file.isFile && (file.name?.endsWith(".txt") == true)) {
                txtFileList.add(file)
            }
        }
        val artist = (pickedDir.name ?: "").trim { it <= ' ' }
        val songs = arrayListOf<Song>()
        txtFileList.forEach { file ->
            try {
                val sc = Scanner(context.contentResolver.openInputStream(file.uri))
                val text = sc.useDelimiter("\\A").next()
                val title = file.name?.replace("\\.txt$".toRegex(), "")?.trim() ?: ""
                val song = Song().apply {
                    this.artist = artist
                    this.title = title
                    this.text = text
                }
                songs.add(song)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                val fileName = file.name ?: ""
                onFileNotFound(fileName)
            }
        }
        return artist to songs
    }

    override fun getSongsFromDir(
        dir: File,
        onFileNotFound: (String) -> Unit
    ): Pair<String, List<Song>> {
        val files = dir.listFiles()
        val txtFileList = arrayListOf<File>()
        files?.forEach { file ->
            if (file.exists() && file.isFile && file.name.endsWith(".txt")) {
                txtFileList.add(file)
            }
        }
        val artist = (dir.name ?: "").trim { it <= ' ' }
        val songs = arrayListOf<Song>()
        txtFileList.forEach { file ->
            try {
                val sc = Scanner(file.inputStream())
                val text = sc.useDelimiter("\\A").next()
                val title = file.name.replace("\\.txt$".toRegex(), "").trim()
                val song = Song().apply {
                    this.artist = artist
                    this.title = title
                    this.text = text
                }
                songs.add(song)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                onFileNotFound(file.name)
            }
        }
        return artist to songs
    }
}