package jatx.russianrocksongbook.database.converters

import jatx.russianrocksongbook.database.db.entities.SongEntity
import jatx.russianrocksongbook.domain.models.local.Song

internal fun Song.toSongEntity() = SongEntity(
    id, artist, title, text, favorite, deleted, outOfTheBox, origTextMD5
)

