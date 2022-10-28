package jatx.russianrocksongbook.networking.converters

import jatx.russianrocksongbook.domain.models.appcrash.AppCrash
import jatx.russianrocksongbook.domain.models.cloud.CloudSong
import jatx.russianrocksongbook.domain.models.warning.TYPE_CLOUD
import jatx.russianrocksongbook.domain.models.warning.Warning
import jatx.russianrocksongbook.networking.gson.CloudSongGson
import org.junit.Assert.assertEquals
import org.junit.Test

const val APP_VERSION_NAME = "1.0"
const val APP_VERSION_CODE = 1
const val ANDROID_VERSION = "10"
const val MANUFACTURER = "Huawei"
const val PRODUCT = "Honor 20"
val STACK_TRACE = IllegalStateException("asfdsf").stackTraceToString()

const val ID = 137
const val ARTIST = "some artist"
const val TITLE = "some title"
const val TEXT = "some text"

const val GOOGLE_ACCOUNT = "e.tabatsky@gmail.com"
const val DEVICE_ID_HASH = "absals2341"
const val IS_USER_SONG = true
const val VARIANT = 7
const val RAITING = 3.14

const val LIKE_COUNT = 31
const val DISLIKE_COUNT = 13

const val COMMENT = "some comment"

class ConvertersTest {
    @Test
    fun appCrash_toAppCrashJson_isWorkingCorrect() {
        val appCrash = AppCrash(
            appVersionName = APP_VERSION_NAME,
            appVersionCode = APP_VERSION_CODE,
            androidVersion = ANDROID_VERSION,
            manufacturer = MANUFACTURER,
            product = PRODUCT,
            stackTrace = STACK_TRACE
        )

        val appCrashGson = appCrash.toAppCrashGson()

        assertEquals(appCrashGson.appVersionName, APP_VERSION_NAME)
        assertEquals(appCrashGson.appVersionCode, APP_VERSION_CODE)
        assertEquals(appCrashGson.androidVersion, ANDROID_VERSION)
        assertEquals(appCrashGson.manufacturer, MANUFACTURER)
        assertEquals(appCrashGson.product, PRODUCT)
        assertEquals(appCrashGson.stackTrace, STACK_TRACE)
    }

    @Test
    fun cloudSong_toCloudSongGson_isWorkingCorrect() {
        val cloudSong = CloudSong(
            songId = ID,
            googleAccount = GOOGLE_ACCOUNT,
            deviceIdHash = DEVICE_ID_HASH,
            artist = ARTIST,
            title = TITLE,
            text = TEXT,
            isUserSong = IS_USER_SONG,
            variant = VARIANT,
            raiting = RAITING
        )

        val cloudSongGson = cloudSong.toCloudSongGson()

        assertEquals(cloudSongGson.songId, ID)
        assertEquals(cloudSongGson.googleAccount, GOOGLE_ACCOUNT)
        assertEquals(cloudSongGson.deviceIdHash, DEVICE_ID_HASH)
        assertEquals(cloudSongGson.artist, ARTIST)
        assertEquals(cloudSongGson.title, TITLE)
        assertEquals(cloudSongGson.text, TEXT)
        assertEquals(cloudSongGson.isUserSong, IS_USER_SONG)
        assertEquals(cloudSongGson.variant, VARIANT)
        assertEquals(cloudSongGson.raiting, RAITING, 0.001)
    }

    @Test
    fun cloudSongGson_toCloudSong_isWorkingCorrect() {
        val cloudSongGson = CloudSongGson(
            songId = ID,
            googleAccount = GOOGLE_ACCOUNT,
            deviceIdHash = DEVICE_ID_HASH,
            artist = ARTIST,
            title = TITLE,
            text = TEXT,
            isUserSong = IS_USER_SONG,
            variant = VARIANT,
            raiting = RAITING,
            likeCount = LIKE_COUNT,
            dislikeCount = DISLIKE_COUNT
        )

        val cloudSong = cloudSongGson.toCloudSong()

        assertEquals(cloudSong.songId, ID)
        assertEquals(cloudSong.googleAccount, GOOGLE_ACCOUNT)
        assertEquals(cloudSong.deviceIdHash, DEVICE_ID_HASH)
        assertEquals(cloudSong.artist, ARTIST)
        assertEquals(cloudSong.title, TITLE)
        assertEquals(cloudSong.text, TEXT)
        assertEquals(cloudSong.isUserSong, IS_USER_SONG)
        assertEquals(cloudSong.variant, VARIANT)
        assertEquals(cloudSong.raiting, RAITING, 0.001)
        assertEquals(cloudSong.likeCount, LIKE_COUNT)
        assertEquals(cloudSong.dislikeCount, DISLIKE_COUNT)
    }

    @Test
    fun warning_toWarningGson_isWorkingCorrect() {
        val cloudSong = CloudSong(
            artist = ARTIST,
            title = TITLE,
            variant = VARIANT,
        )

        val warning = Warning(
            cloudSong = cloudSong,
            comment = COMMENT
        )

        val warningGson = warning.toWarningGson()

        assertEquals(warningGson.warningType, TYPE_CLOUD)
        assertEquals(warningGson.artist, ARTIST)
        assertEquals(warningGson.title, TITLE)
        assertEquals(warningGson.variant, VARIANT)
        assertEquals(warningGson.comment, COMMENT)
    }
}