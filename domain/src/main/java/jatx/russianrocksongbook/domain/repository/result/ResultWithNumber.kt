package jatx.russianrocksongbook.domain.repository.result

data class ResultWithNumber(
    val status: String,
    val message: String?,
    val data: Number?
)