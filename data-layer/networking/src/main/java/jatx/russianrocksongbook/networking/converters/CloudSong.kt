package jatx.russianrocksongbook.networking.converters

import jatx.russianrocksongbook.domain.models.cloud.CloudSong
import jatx.russianrocksongbook.networking.gson.CloudSongGson

internal fun CloudSong.toCloudSongGson() = CloudSongGson(
    songId,
    googleAccount,
    deviceIdHash,
    artist,
    title,
    text,
    textHash,
    isUserSong,
    variant,
    raiting
)
