package jatx.russianrocksongbook.model.api.gson

data class SongBookGson(
    val songbook: List<SongGson>
)

data class SongGson(
    val title: String,
    val text: String
)