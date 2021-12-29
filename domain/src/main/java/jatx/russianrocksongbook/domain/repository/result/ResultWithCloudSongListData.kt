package jatx.russianrocksongbook.domain.repository.result

import jatx.russianrocksongbook.domain.models.CloudSong

data class ResultWithCloudSongListData(
    val status: String,
    val message: String?,
    val data: List<CloudSong>?
)