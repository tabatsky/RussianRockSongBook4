package jatx.russianrocksongbook.database.internal.db.util.gson

internal data class SongBookGson(
    val songbook: List<SongGson>
)

internal data class SongGson(
    val title: String,
    val text: String
)