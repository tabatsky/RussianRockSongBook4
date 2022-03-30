package jatx.russianrocksongbook.networking.ext

import jatx.russianrocksongbook.domain.models.CloudSong
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
