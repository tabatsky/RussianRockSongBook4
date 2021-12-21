package jatx.russianrocksongbook.networking.ext

import jatx.russianrocksongbook.domain.CloudSong
import jatx.russianrocksongbook.domain.Song
import jatx.russianrocksongbook.domain.USER_SONG_MD5
import jatx.russianrocksongbook.domain.util.songTextHash
import jatx.russianrocksongbook.networking.gson.CloudSongGson
import jatx.russianrocksongbook.preferences.UserInfo

fun Song.toCloudSong(userInfo: UserInfo) = CloudSong(
    googleAccount = userInfo.googleAccount,
    deviceIdHash = userInfo.deviceIdHash,
    artist = artist,
    title = title,
    text = text,
    textHash = songTextHash(text),
    isUserSong = origTextMD5 == USER_SONG_MD5
)

fun CloudSongGson.toCloudSong() = CloudSong(
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

fun CloudSong.toCloudSongGson() = CloudSongGson(
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
