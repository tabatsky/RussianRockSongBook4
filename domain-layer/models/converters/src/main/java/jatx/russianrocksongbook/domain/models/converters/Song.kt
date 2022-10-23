package jatx.russianrocksongbook.domain.models.converters

import jatx.russianrocksongbook.domain.models.cloud.CloudSong
import jatx.russianrocksongbook.domain.models.local.Song
import jatx.russianrocksongbook.domain.models.local.USER_SONG_MD5
import jatx.russianrocksongbook.domain.models.cloud.UserInfo
import jatx.russianrocksongbook.domain.models.local.songTextHash

infix fun Song.withUserInfo(userInfo: UserInfo) = CloudSong(
    googleAccount = userInfo.googleAccount,
    deviceIdHash = userInfo.deviceIdHash,
    artist = artist,
    title = title,
    text = text,
    textHash = songTextHash(text),
    isUserSong = origTextMD5 == USER_SONG_MD5
)