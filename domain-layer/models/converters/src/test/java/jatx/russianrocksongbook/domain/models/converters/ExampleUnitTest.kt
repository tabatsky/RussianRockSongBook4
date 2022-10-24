package jatx.russianrocksongbook.domain.models.converters

import jatx.russianrocksongbook.domain.models.cloud.UserInfo
import jatx.russianrocksongbook.domain.models.local.Song
import org.junit.Assert.assertEquals
import org.junit.Test

const val ARTIST = "some artist"
const val TITLE = "some title"
const val TEXT = "some text"
const val GOOGLE_ACCOUNT = "e.tabatsky@gmail.com"
const val DEVICE_ID_HASH = "absals2341"

class ExampleUnitTest {
    @Test
    fun withUserInfo_isWorkingCorrect() {
        val song = Song(
            artist = ARTIST,
            title = TITLE,
            text = TEXT
        )
        val userInfo = object : UserInfo {
            override val deviceIdHash = DEVICE_ID_HASH
            override val googleAccount = GOOGLE_ACCOUNT
        }
        val cloudSong = song withUserInfo userInfo

        assertEquals(cloudSong.artist, ARTIST)
        assertEquals(cloudSong.title, TITLE)
        assertEquals(cloudSong.text, TEXT)
        assertEquals(cloudSong.googleAccount, GOOGLE_ACCOUNT)
        assertEquals(cloudSong.deviceIdHash, DEVICE_ID_HASH)
    }
}