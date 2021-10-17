package jatx.russianrocksongbook.gson

data class SongBook(
    val songbook: List<JsonSong>
)

data class JsonSong(
    val title: String,
    val text: String
)