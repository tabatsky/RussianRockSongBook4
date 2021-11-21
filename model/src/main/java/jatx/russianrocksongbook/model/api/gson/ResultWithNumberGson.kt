package jatx.russianrocksongbook.model.api.gson

data class ResultWithNumberGson(
    val status: String,
    val message: String?,
    val data: Number?
)