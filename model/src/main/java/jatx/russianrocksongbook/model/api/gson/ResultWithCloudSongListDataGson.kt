package jatx.russianrocksongbook.model.api.gson

data class ResultWithCloudSongListDataGson(
    val status: String,
    val message: String?,
    val data: List<CloudSongGson>?
)