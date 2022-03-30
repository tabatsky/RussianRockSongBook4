package jatx.russianrocksongbook.database.ext

import jatx.russianrocksongbook.database.gson.SongGson
import jatx.russianrocksongbook.domain.models.Song
import jatx.russianrocksongbook.util.hashing.songTextHash

internal fun SongGson.toSong(artist: String) = Song(
    artist = artist,
    title = title,
    text = text,
    origTextMD5 = songTextHash(text)
)