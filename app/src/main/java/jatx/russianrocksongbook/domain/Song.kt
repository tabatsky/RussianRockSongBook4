package jatx.russianrocksongbook.domain

import jatx.russianrocksongbook.data.gson.JsonSong
import jatx.russianrocksongbook.db.entities.SongEntity
import jatx.russianrocksongbook.domain.util.HashingUtil

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
    constructor(artist: String, jsonSong: JsonSong):
            this(
                artist = artist,
                title = jsonSong.title,
                text = jsonSong.text,
                origTextMD5 = songTextHash(jsonSong.text)
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
}

fun songTextHash(text: String): String {
    val preparedText =
        text.trim { it <= ' ' }.lowercase().replace("\\s+".toRegex(), " ")
    return HashingUtil.md5(preparedText)
}