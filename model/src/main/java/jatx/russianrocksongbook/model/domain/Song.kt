package jatx.russianrocksongbook.model.domain

import jatx.russianrocksongbook.model.api.gson.SongGson
import jatx.russianrocksongbook.model.db.entities.SongEntity
import jatx.russianrocksongbook.model.domain.util.HashingUtil

const val USER_SONG_MD5 = "USER"

data class Song(
    var id: Long? = null,
    var artist: String = "",
    var title: String = "",
    var text: String = "",
    var favorite: Boolean = false,
    var deleted: Boolean = false,
    var outOfTheBox: Boolean = true,
    var origTextMD5: String = ""
) {
    constructor(artist: String, songGson: SongGson):
            this(
                artist = artist,
                title = songGson.title,
                text = songGson.text,
                origTextMD5 = songTextHash(songGson.text)
            )

    constructor(songEntity: SongEntity):
            this(
                id = songEntity.id,
                artist = songEntity.artist,
                title = songEntity.title,
                text = songEntity.text,
                favorite = songEntity.favorite,
                deleted = songEntity.deleted,
                outOfTheBox = songEntity.outOfTheBox,
                origTextMD5 = songEntity.origTextMD5
            )

    fun toSongEntity() = SongEntity(
        id, artist, title, text, favorite, deleted, outOfTheBox, origTextMD5
    )

    // for correct MutableStateFlow working
    override fun equals(other: Any?): Boolean {
        return super.equals(other) && other is Song && favorite == other.favorite
    }

    override fun hashCode(): Int {
        return super.hashCode() + (if (favorite) 1 else 0)
    }
}

fun songTextHash(text: String): String {
    val preparedText =
        text.trim { it <= ' ' }.lowercase().replace("\\s+".toRegex(), " ")
    return HashingUtil.md5(preparedText)
}