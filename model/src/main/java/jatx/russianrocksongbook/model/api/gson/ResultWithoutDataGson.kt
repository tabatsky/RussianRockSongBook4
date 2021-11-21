package jatx.russianrocksongbook.model.api.gson

const val STATUS_SUCCESS = "success"
const val STATUS_ERROR = "error"

data class ResultWithoutDataGson(
    val status: String,
    val message: String?
)