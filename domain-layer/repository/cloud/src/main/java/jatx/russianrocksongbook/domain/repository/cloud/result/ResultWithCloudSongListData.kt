package jatx.russianrocksongbook.domain.repository.cloud.result

import jatx.russianrocksongbook.domain.models.cloud.CloudSong

data class ResultWithCloudSongListData(
    val status: String,
    val message: String?,
    val data: List<CloudSong>?
)