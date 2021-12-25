package jatx.russianrocksongbook.networking.api.result

data class ResultWithNumber(
    val status: String,
    val message: String?,
    val data: Number?
)