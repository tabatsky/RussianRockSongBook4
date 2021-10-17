package jatx.russianrocksongbook.db.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import jatx.russianrocksongbook.db.util.HashingUtil
import jatx.russianrocksongbook.gson.JsonSong
import org.w3c.dom.Text

const val USER_SONG_MD5 = "USER"

@Entity(tableName = "songs", indices = arrayOf(Index(name = "the_index", value = arrayOf("artist", "title"), unique = true)))
data class Song(
    @PrimaryKey
    val id: Long? = null,
    val artist: String = "",
    val title: String = "",
    val text: String = "",
    val favorite: Boolean = false,
    val deleted: Boolean = false,
    val outOfTheBox: Boolean = true,
    val origTextMD5: String = ""
) {

    constructor(artist: String, jsonSong: JsonSong):
            this(
                artist = artist,
                title = jsonSong.title,
                text = jsonSong.text,
                origTextMD5 = songTextHash(jsonSong.text)
            )

    fun withArtist(artist: String) = copy(artist = artist)
    fun withTitle(title: String) = copy(title = title)
    fun withText(text: String) = copy(text = text)
    fun withFavorite(value: Boolean) = copy(favorite = value)
    fun withDeleted(value: Boolean) = copy(deleted = value)
    fun withOutOfTheBox(value: Boolean) = copy(outOfTheBox = value)
    fun withOrigTextMD5(origTextMD5: String) = copy(origTextMD5 = origTextMD5)
}

fun songTextHash(text: String): String {
    val preparedText =
        text.trim { it <= ' ' }.lowercase().replace("\\s+".toRegex(), " ")
    return HashingUtil.md5(preparedText)
}
