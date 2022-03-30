package jatx.russianrocksongbook.database.ext

import jatx.russianrocksongbook.database.db.entities.SongEntity
import jatx.russianrocksongbook.domain.models.Song

internal fun Song.toSongEntity() = SongEntity(
    id, artist, title, text, favorite, deleted, outOfTheBox, origTextMD5
)

