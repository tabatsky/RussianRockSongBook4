package jatx.russianrocksongbook.networking.apimodels

import jatx.russianrocksongbook.domain.repository.cloud.result.ResultWithCloudSongListData
import jatx.russianrocksongbook.networking.converters.toCloudSong

internal data class ResultWithCloudSongApiModelListData(
    val status: String,
    val message: String?,
    val data: List<CloudSongApiModel>?
)

internal fun ResultWithCloudSongApiModelListData
        .toResultWithCloudSongListData() =
    ResultWithCloudSongListData(
        status = status,
        message = message,
        data = data?.map { it.toCloudSong() }
    )