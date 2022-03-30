package jatx.russianrocksongbook.networking.ext

import jatx.russianrocksongbook.domain.models.CloudSong
import jatx.russianrocksongbook.networking.gson.CloudSongGson

internal fun CloudSongGson.toCloudSong() = CloudSong(
    songId = songId,
    googleAccount = googleAccount,
    deviceIdHash = deviceIdHash,
    artist = artist,
    title = title,
    text = text,
    textHash = textHash,
    isUserSong = isUserSong,
    variant = variant,
    raiting = raiting,
    likeCount = likeCount,
    dislikeCount = dislikeCount
)