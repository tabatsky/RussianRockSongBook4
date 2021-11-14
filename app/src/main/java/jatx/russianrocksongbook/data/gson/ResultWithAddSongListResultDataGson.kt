package jatx.russianrocksongbook.data.gson

data class ResultWithAddSongListResultDataGson(
    val status: String,
    val message: String?,
    val data: AddSongListResult?
)

data class AddSongListResult(
    val success: Int,
    val duplicate: Int,
    val error: Int
)