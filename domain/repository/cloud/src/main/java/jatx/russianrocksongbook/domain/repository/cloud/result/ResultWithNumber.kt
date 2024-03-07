package jatx.russianrocksongbook.domain.repository.cloud.result

data class ResultWithNumber(
    val status: String,
    val message: String?,
    val data: Number?
)