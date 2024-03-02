package jatx.russianrocksongbook.networking.converters

import jatx.russianrocksongbook.domain.models.cloud.CloudSong
import jatx.russianrocksongbook.networking.apimodels.CloudSongApiModel

internal fun CloudSongApiModel.toCloudSong() = CloudSong(
    songId = songId,
    googleAccount = googleAccount,
    deviceIdHash = deviceIdHash,
    artist = artist.trim(),
    title = title.trim(),
    text = text,
    textHash = textHash,
    isUserSong = isUserSong,
    variant = variant,
    raiting = raiting,
    likeCount = likeCount,
    dislikeCount = dislikeCount
)