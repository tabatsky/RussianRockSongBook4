package jatx.russianrocksongbook.networking.repository

import com.google.gson.Gson
import io.mockk.coEvery
import io.mockk.coVerifySequence
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.junit5.MockKExtension
import jatx.russianrocksongbook.domain.models.appcrash.AppCrash
import jatx.russianrocksongbook.domain.models.cloud.UserInfo
import jatx.russianrocksongbook.domain.models.converters.asCloudSongWithUserInfo
import jatx.russianrocksongbook.domain.models.local.Song
import jatx.russianrocksongbook.domain.repository.cloud.CloudRepository
import jatx.russianrocksongbook.domain.repository.cloud.CloudSearchOrderBy
import jatx.russianrocksongbook.networking.converters.toAppCrashApiModel
import jatx.russianrocksongbook.networking.converters.toCloudSongApiModel
import jatx.russianrocksongbook.networking.converters.toWarningApiModel
import jatx.russianrocksongbook.networking.apimodels.ResultWithCloudSongApiModelListData
import jatx.russianrocksongbook.networking.songbookapi.RetrofitClient
import jatx.russianrocksongbook.networking.songbookapi.SongBookAPI
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runners.MethodSorters
import java.util.concurrent.TimeUnit

@MockKExtension.ConfirmVerification
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class CloudRepositoryImplTest {
    @get:Rule
    val mockkRule = MockKRule(this)

    @RelaxedMockK
    lateinit var retrofitClient: RetrofitClient
    @RelaxedMockK
    internal lateinit var songBookAPI: SongBookAPI
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
        every { retrofitClient.songBookAPI } returns songBookAPI
        cloudRepository = CloudRepositoryImpl(retrofitClient)
    }

    @Test
    fun test001_sendCrash_isWorkingCorrect() {
        runBlocking {
            cloudRepository.sendCrash(appCrash)
        }

        TimeUnit.MILLISECONDS.sleep(500)

        val params = mapOf(
            "appCrashJSON" to Gson().toJson(appCrash.toAppCrashApiModel())
        )

        coVerifySequence {
            songBookAPI.sendCrash(params)
        }
    }

    @Test
    fun test002_addCloudSong_isWorkingCorrect() {
        runBlocking {
            cloudRepository.addCloudSong(cloudSong)
        }

        val params = mapOf(
            "cloudSongJSON" to Gson().toJson(cloudSong.toCloudSongApiModel())
        )

        TimeUnit.MILLISECONDS.sleep(500)

        coVerifySequence {
            songBookAPI.addSong(params)
        }
    }

    @Test
    fun test003_addCloudSongList_isWorkingCorrect() {
        runBlocking {
            cloudRepository.addCloudSongList(cloudSongList)
        }

        TimeUnit.MILLISECONDS.sleep(500)

        val cloudSongsGson = cloudSongList.map { it.toCloudSongApiModel() }
        val params = mapOf(
            "cloudSongListJSON" to Gson().toJson(cloudSongsGson)
        )

        coVerifySequence {
            songBookAPI.addSongList(params)
        }
    }

    @Test
    fun test004_addWarning_isWorkingCorrect() {
        val warning = cloudSong.warningWithComment("some comment")

        runBlocking {
            cloudRepository.addWarning(warning)
        }

        TimeUnit.MILLISECONDS.sleep(500)

        val params = mapOf(
            "warningJSON" to Gson().toJson(warning.toWarningApiModel())
        )

        coVerifySequence {
            songBookAPI.addWarning(params)
        }
    }

    @Test
    fun test005_search_isWorkingCorrect() {
        coEvery { songBookAPI.searchSongs(any(), any()) } returns
                ResultWithCloudSongApiModelListData(
                    status = "success",
                    message = null,
                    data = cloudSongList.map { it.toCloudSongApiModel() }
                )

        runBlocking {
            val result = cloudRepository.searchSongs("Сплин", CloudSearchOrderBy.BY_ARTIST)
            assertEquals(cloudSongList, result.data)
        }

        coVerifySequence {
            songBookAPI.searchSongs("Сплин", CloudSearchOrderBy.BY_ARTIST.orderBy)
        }
    }

    @Test
    fun test006_vote_isWorkingCorrect() {
        runBlocking {
            cloudRepository.vote(cloudSong, userInfo, 1)
        }

        TimeUnit.MILLISECONDS.sleep(500)

        coVerifySequence {
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
        runBlocking {
            cloudRepository.delete("gh47", "sd93", cloudSong)
        }

        TimeUnit.MILLISECONDS.sleep(500)

        coVerifySequence {
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
        coEvery { songBookAPI.pagedSearch(any(), any(), any()) } returns
                ResultWithCloudSongApiModelListData(
                    status = "success",
                    message = null,
                    data = cloudSongList.map { it.toCloudSongApiModel() }
                )

        runBlocking {
            val result = cloudRepository.pagedSearch("Сплин", CloudSearchOrderBy.BY_ARTIST, 3)
            assertEquals(cloudSongList, result.data)
        }

        TimeUnit.MILLISECONDS.sleep(500)

        coVerifySequence {
            songBookAPI.pagedSearch("Сплин", CloudSearchOrderBy.BY_ARTIST.orderBy, 3)
        }
    }

    @Test
    fun test009_search_isWorkingCorrect() {
        coEvery { songBookAPI.searchSongs(any(), any()) } returns
                ResultWithCloudSongApiModelListData(
                    status = "success",
                    message = null,
                    data = cloudSongList.map { it.toCloudSongApiModel() }
                )

        runBlocking {
            val result = cloudRepository.search("Сплин", CloudSearchOrderBy.BY_ARTIST)
            assertEquals(cloudSongList, result)
        }

        TimeUnit.MILLISECONDS.sleep(500)

        coVerifySequence {
            songBookAPI.searchSongs("Сплин", CloudSearchOrderBy.BY_ARTIST.orderBy)
        }
    }
}