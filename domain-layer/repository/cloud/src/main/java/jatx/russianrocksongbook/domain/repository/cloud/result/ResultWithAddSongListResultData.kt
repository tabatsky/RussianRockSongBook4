package jatx.russianrocksongbook.domain.repository.cloud.result

data class ResultWithAddSongListResultData(
    val status: String,
    val message: String?,
    val data: AddSongListResult?
)

data class AddSongListResult(
    val success: Int,
    val duplicate: Int,
    val error: Int
)