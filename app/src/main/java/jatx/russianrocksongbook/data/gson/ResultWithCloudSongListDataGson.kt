package jatx.russianrocksongbook.data.gson

data class ResultWithCloudSongListDataGson(
    val status: String,
    val message: String?,
    val data: List<CloudSongGson>?
)