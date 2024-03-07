package jatx.russianrocksongbook.database.converters

import jatx.russianrocksongbook.database.db.entities.SongEntity
import jatx.russianrocksongbook.domain.models.local.Song

internal fun Song.toSongEntity() = SongEntity(
    id = id,
    artist = artist,
    title = title,
    text = text,
    favorite = favorite,
    deleted = deleted,
    outOfTheBox = outOfTheBox,
    origTextMD5 = origTextMD5
)

