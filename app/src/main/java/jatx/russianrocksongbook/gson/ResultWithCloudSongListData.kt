package jatx.russianrocksongbook.gson

data class ResultWithCloudSongListData(
    val status: String,
    val message: String?,
    val data: List<CloudSong>?
)