package jatx.russianrocksongbook.data.result

import jatx.russianrocksongbook.domain.CloudSong

data class ResultWithCloudSongListData(
    val status: String,
    val message: String?,
    val data: List<CloudSong>?
)