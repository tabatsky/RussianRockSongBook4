package jatx.russianrocksongbook.database.converters

import jatx.russianrocksongbook.database.gson.SongGson
import jatx.russianrocksongbook.domain.models.local.Song
import jatx.russianrocksongbook.domain.models.local.songTextHash

internal infix fun SongGson.asSongWithArtist(artist: String) = Song(
    artist = artist,
    title = title,
    text = text,
    origTextMD5 = songTextHash(text)
)