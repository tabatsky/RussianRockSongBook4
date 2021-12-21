package jatx.russianrocksongbook.data.result

data class SongBookGson(
    val songbook: List<SongGson>
)

data class SongGson(
    val title: String,
    val text: String
)