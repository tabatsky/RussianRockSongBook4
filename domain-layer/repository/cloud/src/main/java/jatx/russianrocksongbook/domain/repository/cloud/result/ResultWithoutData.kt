package jatx.russianrocksongbook.domain.repository.cloud.result

const val STATUS_SUCCESS = "success"
const val STATUS_ERROR = "error"

data class ResultWithoutData(
    val status: String,
    val message: String?
)