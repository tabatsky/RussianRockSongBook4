package jatx.russianrocksongbook.domain.usecase.cloud

import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verifySequence
import io.reactivex.Single
import jatx.russianrocksongbook.domain.models.appcrash.AppCrash
import jatx.russianrocksongbook.domain.models.cloud.UserInfo
import jatx.russianrocksongbook.domain.models.converters.asCloudSongWithUserInfo
import jatx.russianrocksongbook.domain.models.local.Song
import jatx.russianrocksongbook.domain.repository.cloud.CloudRepository
import jatx.russianrocksongbook.domain.repository.cloud.OrderBy
import jatx.russianrocksongbook.domain.repository.cloud.result.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters

@MockKExtension.ConfirmVerification
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class CloudUseCaseTest {
    lateinit var cloudRepository: CloudRepository
    lateinit var addSongListToCloudUseCase: AddSongListToCloudUseCase
    lateinit var addSongToCloudUseCase: AddSongToCloudUseCase
    lateinit var addWarningUseCase: AddWarningUseCase
    lateinit var deleteFromCloudUseCase: DeleteFromCloudUseCase
    lateinit var pagedSearchUseCase: PagedSearchUseCase
    lateinit var sendCrashUseCase: SendCrashUseCase
    lateinit var voteUseCase: VoteUseCase

    private val userInfo = object : UserInfo {
        override val deviceIdHash = "sdsf2335"
        override val googleAccount = "e.tabatsky@gmail.com"
    }

    private val song = Song(artist = "artist", title = "title", text = "text")

    private val appCrash = AppCrash(
        appVersionName = "1.0",
        appVersionCode = 1,
        androidVersion = "10.0",
        manufacturer = "Huawei",
        product = "Honor 20",
        stackTrace = Exception().stackTraceToString()
    )

    private val songList = listOf(
        song.copy(title = "title 1"),
        song.copy(title = "title 2"),
        song.copy(title = "title 3"),
        song.copy(title = "title 4"),
        song.copy(title = "title 5")
    )

    private val comment = "comment"

    private val resultWithAddSongListResultData = ResultWithAddSongListResultData(
        status = "success",
        message = null,
        data = AddSongListResult(
            success = 8,
            duplicate = 5,
            error = 2
        )
    )

    private val resultWithoutData = ResultWithoutData(
        status = "success",
        message = null
    )

    private val resultWithNumber = ResultWithNumber(
        status = "success",
        message = null,
        data = 1
    )

    private val resultWithCloudSongListResultData = ResultWithCloudSongListData(
        status = "success",
        message = null,
        data = songList.map { it asCloudSongWithUserInfo userInfo }
    )

    @Before
    fun init() {
        cloudRepository = mockk(relaxed = true)
        addSongListToCloudUseCase = AddSongListToCloudUseCase(
            cloudRepository = cloudRepository,
            userInfo = userInfo
        )
        addSongToCloudUseCase = AddSongToCloudUseCase(
            cloudRepository = cloudRepository,
            userInfo = userInfo
        )
        addWarningUseCase = AddWarningUseCase(cloudRepository)
        deleteFromCloudUseCase = DeleteFromCloudUseCase(cloudRepository)
        pagedSearchUseCase = PagedSearchUseCase(cloudRepository)
        sendCrashUseCase = SendCrashUseCase(cloudRepository)
        voteUseCase = VoteUseCase(
            cloudRepository = cloudRepository,
            userInfo = userInfo
        )
    }

    @Test
    fun test001_addSongListToCloudUseCase_isWorkingCorrect() {
        every { cloudRepository.addCloudSongList(any()) } returns
                Single.just(resultWithAddSongListResultData)

        val result = addSongListToCloudUseCase.execute(songList)

        assertEquals(resultWithAddSongListResultData, result.blockingGet())

        verifySequence {
            cloudRepository.addCloudSongList(
                songList.map {
                    it asCloudSongWithUserInfo userInfo
                }
            )
        }
    }

    @Test
    fun test002_addSongToCloudUseCase_isWorkingCorrect() {
        every { cloudRepository.addCloudSong(any()) } returns
                Single.just(resultWithoutData)

        val result = addSongToCloudUseCase.execute(song)

        assertEquals(resultWithoutData, result.blockingGet())

        verifySequence {
            cloudRepository.addCloudSong(song asCloudSongWithUserInfo userInfo)
        }
    }

    @Test
    fun test003_addWarningCloudUseCase_isWorkingCorrect() {
        every { cloudRepository.addWarning(any()) } returns
                Single.just(resultWithoutData)

        val result = addWarningUseCase.execute(
            warnable = song asCloudSongWithUserInfo userInfo,
            comment = comment
        )

        assertEquals(resultWithoutData, result.blockingGet())

        val warning = (song asCloudSongWithUserInfo userInfo)
            .warningWithComment(comment)

        verifySequence {
            cloudRepository.addWarning(warning)
        }
    }

    @Test
    fun test004_addWarningLocalUseCase_isWorkingCorrect() {
        every { cloudRepository.addWarning(any()) } returns
                Single.just(resultWithoutData)

        val result = addWarningUseCase.execute(
            warnable = song,
            comment = comment
        )

        assertEquals(resultWithoutData, result.blockingGet())

        val warning = song.warningWithComment(comment)

        verifySequence {
            cloudRepository.addWarning(warning)
        }
    }

    @Test
    fun test005_addDeleteFromCloudUseCase_isWorkingCorrect() {
        every { cloudRepository.delete(any(), any(), any()) } returns
                Single.just(resultWithNumber)

        val result = deleteFromCloudUseCase.execute(
            secret1 = "gh56",
            secret2 = "io89",
            cloudSong = song asCloudSongWithUserInfo userInfo
        )

        assertEquals(resultWithNumber, result.blockingGet())

        verifySequence {
            cloudRepository.delete(
                secret1 = "gh56",
                secret2 = "io89",
                cloudSong = song asCloudSongWithUserInfo userInfo
            )
        }
    }

    @Test
    fun test006_pagedSearchUseCase_isWorkingCorrect() {
        every { cloudRepository.pagedSearch(any(), any(), any()) } returns
                resultWithCloudSongListResultData

        val result = pagedSearchUseCase.execute("Кино", OrderBy.BY_ARTIST, 3)

        assertEquals(resultWithCloudSongListResultData, result)

        verifySequence {
            cloudRepository.pagedSearch("Кино", OrderBy.BY_ARTIST, 3)
        }
    }

    @Test
    fun test007_sendCrashUseCase_isWorkingCorrect() {
        every { cloudRepository.sendCrash(any()) } returns
                Single.just(resultWithoutData)

        val result = sendCrashUseCase.execute(appCrash)

        assertEquals(resultWithoutData, result.blockingGet())

        verifySequence {
            cloudRepository.sendCrash(appCrash)
        }
    }

    @Test
    fun test008_voteUseCase_isWorkingCorrect() {
        every { cloudRepository.vote(any(), any(), any()) } returns
                Single.just(resultWithNumber)

        val result = voteUseCase.execute(
            cloudSong = song asCloudSongWithUserInfo userInfo,
            voteValue = 1
        )

        assertEquals(resultWithNumber, result.blockingGet())

        verifySequence {
            cloudRepository.vote(
                cloudSong = song asCloudSongWithUserInfo userInfo,
                userInfo = userInfo,
                voteValue = 1
            )
        }
    }
}