package jatx.russianrocksongbook.networking.gson

import jatx.russianrocksongbook.domain.repository.cloud.result.ResultWithCloudSongListData
import jatx.russianrocksongbook.networking.converters.toCloudSong

internal data class ResultWithCloudSongGsonListData(
    val status: String,
    val message: String?,
    val data: List<CloudSongGson>?
)

internal fun ResultWithCloudSongGsonListData
        .toResultWithCloudSongListData() =
    ResultWithCloudSongListData(
        status = status,
        message = message,
        data = data?.map { it.toCloudSong() }
    )