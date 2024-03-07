package jatx.russianrocksongbook.database.converters

import jatx.russianrocksongbook.database.dbinit.jsonresourcemodel.SongJsonResourceModel
import jatx.russianrocksongbook.domain.models.local.Song
import jatx.russianrocksongbook.domain.models.local.songTextHash

internal infix fun SongJsonResourceModel.asSongWithArtist(artist: String) = Song(
    artist = artist,
    title = title,
    text = text,
    origTextMD5 = songTextHash(text)
)