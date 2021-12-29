package jatx.russianrocksongbook.networking.ext

import jatx.russianrocksongbook.domain.models.CloudSong
import jatx.russianrocksongbook.domain.models.Song
import jatx.russianrocksongbook.domain.models.USER_SONG_MD5
import jatx.russianrocksongbook.util.hashing.songTextHash
import jatx.russianrocksongbook.domain.models.interfaces.UserInfo

internal fun Song.toCloudSong(userInfo: UserInfo) = CloudSong(
    googleAccount = userInfo.googleAccount,
    deviceIdHash = userInfo.deviceIdHash,
    artist = artist,
    title = title,
    text = text,
    textHash = songTextHash(text),
    isUserSong = origTextMD5 == USER_SONG_MD5
)