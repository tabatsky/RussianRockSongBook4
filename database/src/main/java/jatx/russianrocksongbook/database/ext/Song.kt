package jatx.russianrocksongbook.database.ext

import jatx.russianrocksongbook.database.db.entities.SongEntity
import jatx.russianrocksongbook.domain.Song
import jatx.russianrocksongbook.domain.util.songTextHash
import jatx.russianrocksongbook.data.result.SongGson

fun Song.toSongEntity() = SongEntity(
    id, artist, title, text, favorite, deleted, outOfTheBox, origTextMD5
)

fun SongEntity.toSong() = Song(
    id = id,
    artist = artist,
    title = title,
    text = text,
    favorite = favorite,
    deleted = deleted,
    outOfTheBox = outOfTheBox,
    origTextMD5 = origTextMD5
)

fun SongGson.toSong(artist: String) = Song(
    artist = artist,
    title = title,
    text = text,
    origTextMD5 = songTextHash(text)
)
