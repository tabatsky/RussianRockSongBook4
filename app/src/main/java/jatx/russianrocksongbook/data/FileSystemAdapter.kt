package jatx.russianrocksongbook.data

import android.content.Context
import androidx.documentfile.provider.DocumentFile
import jatx.russianrocksongbook.db.entities.Song
import java.io.File
import java.io.FileNotFoundException
import java.util.*

class FileSystemAdapter(
    val context: Context,
    val songRepo: SongRepository
) {
    fun getSongsFromDir(
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
                val sc = Scanner(context.contentResolver.openInputStream(file.getUri()))
                val text = sc.useDelimiter("\\A").next()
                val title = file.name?.replace("\\.txt$".toRegex(), "")?.trim() ?: ""
                val song = Song()
                    .withArtist(artist)
                    .withTitle(title)
                    .withText(text)
                songs.add(song)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                val fileName = file.name ?: ""
                onFileNotFound(fileName)
            }
        }
        return artist to songs
    }

    fun getSongsFromDir(
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
                val song = Song()
                    .withArtist(artist)
                    .withTitle(title)
                    .withText(text)
                songs.add(song)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                onFileNotFound(file.name)
            }
        }
        return artist to songs
    }
}