package jatx.russianrocksongbook.networking.repository

import com.google.gson.Gson
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verifySequence
import io.reactivex.Single
import jatx.russianrocksongbook.domain.models.appcrash.AppCrash
import jatx.russianrocksongbook.domain.models.cloud.UserInfo
import jatx.russianrocksongbook.domain.models.converters.asCloudSongWithUserInfo
import jatx.russianrocksongbook.domain.models.local.Song
import jatx.russianrocksongbook.domain.models.warning.Warning
import jatx.russianrocksongbook.domain.repository.cloud.CloudRepository
import jatx.russianrocksongbook.domain.repository.cloud.OrderBy
import jatx.russianrocksongbook.networking.converters.toAppCrashGson
import jatx.russianrocksongbook.networking.converters.toCloudSongGson
import jatx.russianrocksongbook.networking.converters.toWarningGson
import jatx.russianrocksongbook.networking.gson.ResultWithCloudSongGsonListData
import jatx.russianrocksongbook.networking.songbookapi.RetrofitClient
import jatx.russianrocksongbook.networking.songbookapi.SongBookAPI
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters

@MockKExtension.ConfirmVerification
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class CloudRepositoryImplTest {
    private lateinit var retrofitClient: RetrofitClient
    private lateinit var songBookAPI: SongBookAPI
    private lateinit var cloudRepository: CloudRepository

    private val appCrash = AppCrash(
        appVersionName = "1.0",
        appVersionCode = 1,
        androidVersion = "10.0",
        manufacturer = "Huawei",
        product = "Honor 20",
        stackTrace = Exception().stackTraceToString()
    )

    private val userInfo = object : UserInfo {
        override val deviceIdHash = "sdsf2335"
        override val googleAccount = "e.tabatsky@gmail.com"
    }

    private val song = Song(artist = "artist", title = "title", text = "text")

    private val cloudSong = song asCloudSongWithUserInfo userInfo

    private val cloudSongList = listOf(
        cloudSong.copy(title = "title1"),
        cloudSong.copy(title = "title2"),
        cloudSong.copy(title = "title3"),
        cloudSong.copy(title = "title4"),
        cloudSong.copy(title = "title5")
    )

    @Before
    fun init() {
        retrofitClient = mockk(relaxed = true)
        songBookAPI = mockk(relaxed = true)
        every { retrofitClient.songBookAPI } returns songBookAPI
        cloudRepository = CloudRepositoryImpl(retrofitClient)
    }

    @Test
    fun test001_sendCrash_isWorkingCorrect() {
        cloudRepository.sendCrash(appCrash)

        val params = mapOf(
            "appCrashJSON" to Gson().toJson(appCrash.toAppCrashGson())
        )

        verifySequence {
            songBookAPI.sendCrash(params)
        }
    }

    @Test
    fun test002_addCloudSong_isWorkingCorrect() {
        cloudRepository.addCloudSong(cloudSong)

        val params = mapOf(
            "cloudSongJSON" to Gson().toJson(cloudSong.toCloudSongGson())
        )

        verifySequence {
            songBookAPI.addSong(params)
        }
    }

    @Test
    fun test003_addCloudSongList_isWorkingCorrect() {
        cloudRepository.addCloudSongList(cloudSongList)

        val cloudSongsGson = cloudSongList.map { it.toCloudSongGson() }
        val params = mapOf(
            "cloudSongListJSON" to Gson().toJson(cloudSongsGson)
        )

        verifySequence {
            songBookAPI.addSongList(params)
        }
    }

    @Test
    fun test004_addWarning_isWorkingCorrect() {
        val warning = Warning(
            cloudSong = cloudSong,
            comment = "some comment"
        )

        cloudRepository.addWarning(warning)

        val params = mapOf(
            "warningJSON" to Gson().toJson(warning.toWarningGson())
        )

        verifySequence {
            songBookAPI.addWarning(params)
        }
    }

    @Test
    fun test005_searchSongs_isWorkingCorrect() {
        every { songBookAPI.searchSongs(any(), any()) } returns
                Single.just(
                    ResultWithCloudSongGsonListData(
                        status = "success",
                        message = null,
                        data = cloudSongList.map { it.toCloudSongGson() }
                    )
                )

        val result = cloudRepository.searchSongs("Сплин", OrderBy.BY_ARTIST)

        assertEquals(cloudSongList, result.blockingGet().data)

        verifySequence {
            songBookAPI.searchSongs("Сплин", OrderBy.BY_ARTIST.orderBy)
        }
    }

    @Test
    fun test006_vote_isWorkingCorrect() {
        cloudRepository.vote(cloudSong, userInfo, 1)

        verifySequence {
            songBookAPI.vote(
                userInfo.googleAccount,
                userInfo.deviceIdHash,
                cloudSong.artist,
                cloudSong.title,
                cloudSong.variant,
                1
            )
        }
    }

    @Test
    fun test007_delete_isWorkingCorrect() {
        cloudRepository.delete("gh47", "sd93", cloudSong)

        verifySequence {
            songBookAPI.delete(
                "gh47",
                "sd93",
                cloudSong.artist,
                cloudSong.title,
                cloudSong.variant
            )
        }
    }

    @Test
    fun test008_pagedSearch_isWorkingCorrect() {
        every { songBookAPI.pagedSearch(any(), any(), any()) } returns
                ResultWithCloudSongGsonListData(
                    status = "success",
                    message = null,
                    data = cloudSongList.map { it.toCloudSongGson() }
                )

        val result = cloudRepository.pagedSearch("Сплин", OrderBy.BY_ARTIST, 3)

        assertEquals(cloudSongList, result.data)

        verifySequence {
            songBookAPI.pagedSearch("Сплин", OrderBy.BY_ARTIST.orderBy, 3)
        }
    }

    @Test
    fun test009_search_isWorkingCorrect() {
        every { songBookAPI.searchSongs(any(), any()) } returns
                Single.just(
                    ResultWithCloudSongGsonListData(
                        status = "success",
                        message = null,
                        data = cloudSongList.map { it.toCloudSongGson() }
                    )
                )

        val result = cloudRepository.search("Сплин", OrderBy.BY_ARTIST)

        assertEquals(cloudSongList, result)

        verifySequence {
            songBookAPI.searchSongs("Сплин", OrderBy.BY_ARTIST.orderBy)
        }
    }
}