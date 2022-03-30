package jatx.russianrocksongbook.networking.gson

import jatx.russianrocksongbook.domain.repository.result.ResultWithCloudSongListData
import jatx.russianrocksongbook.networking.ext.toCloudSong

data class ResultWithCloudSongGsonListData(
    val status: String,
    val message: String?,
    val data: List<CloudSongGson>?
)

fun ResultWithCloudSongGsonListData
        .toResultWithCloudSongListData() =
    ResultWithCloudSongListData(
        status = status,
        message = message,
        data = data?.map { it.toCloudSong() }
    )