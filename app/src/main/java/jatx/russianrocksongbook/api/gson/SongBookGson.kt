package jatx.russianrocksongbook.api.gson

data class SongBookGson(
    val songbook: List<JsonSong>
)

data class JsonSong(
    val title: String,
    val text: String
)