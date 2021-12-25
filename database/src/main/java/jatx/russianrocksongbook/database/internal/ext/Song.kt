package jatx.russianrocksongbook.database.internal.ext

import jatx.russianrocksongbook.database.internal.db.entities.SongEntity
import jatx.russianrocksongbook.domain.Song
import jatx.russianrocksongbook.domain.util.songTextHash
import jatx.russianrocksongbook.database.internal.db.util.gson.SongGson

internal fun Song.toSongEntity() = SongEntity(
    id, artist, title, text, favorite, deleted, outOfTheBox, origTextMD5
)

internal fun SongEntity.toSong() = Song(
    id = id,
    artist = artist,
    title = title,
    text = text,
    favorite = favorite,
    deleted = deleted,
    outOfTheBox = outOfTheBox,
    origTextMD5 = origTextMD5
)

internal fun SongGson.toSong(artist: String) = Song(
    artist = artist,
    title = title,
    text = text,
    origTextMD5 = songTextHash(text)
)
