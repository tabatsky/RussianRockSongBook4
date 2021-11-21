package jatx.russianrocksongbook.db.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "songs", indices = [Index(name = "the_index", value = arrayOf("artist", "title"), unique = true)])
data class SongEntity(
    @PrimaryKey
    val id: Long? = null,
    val artist: String = "",
    val title: String = "",
    val text: String = "",
    val favorite: Boolean = false,
    val deleted: Boolean = false,
    val outOfTheBox: Boolean = true,
    val origTextMD5: String = ""
)

