package jatx.russianrocksongbook.networking.internal.gson

import jatx.russianrocksongbook.networking.api.result.ResultWithCloudSongListData
import jatx.russianrocksongbook.networking.internal.ext.toCloudSong

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