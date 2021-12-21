package jatx.russianrocksongbook.data.result

data class ResultWithNumber(
    val status: String,
    val message: String?,
    val data: Number?
)