package jatx.russianrocksongbook.viewmodel

import android.util.Log
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.junit5.MockKExtension
import jatx.russianrocksongbook.domain.repository.local.ARTIST_FAVORITE
import jatx.russianrocksongbook.domain.repository.preferences.SettingsRepository
import jatx.russianrocksongbook.domain.usecase.local.GetArtistsUseCase
import jatx.russianrocksongbook.testutils.TestViewModelScopeRule
import jatx.russianrocksongbook.viewmodel.deps.Callbacks
import jatx.russianrocksongbook.viewmodel.deps.Resources
import jatx.russianrocksongbook.viewmodel.deps.TVDetector
import jatx.russianrocksongbook.viewmodel.deps.Toasts
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.runners.MethodSorters
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString

@MockKExtension.ConfirmVerification
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
open class CommonViewModelTest {
    @get:Rule
    val testViewModelScopeRule = TestViewModelScopeRule()

    @get:Rule
    val mockkRule = MockKRule(this)

    @RelaxedMockK
    lateinit var settingsRepository: SettingsRepository

    @RelaxedMockK
    lateinit var callbacks: Callbacks

    @RelaxedMockK
    lateinit var resources: Resources

    @RelaxedMockK
    lateinit var toasts: Toasts

    @RelaxedMockK
    lateinit var tvDetector: TVDetector

    @RelaxedMockK
    lateinit var getArtistsUseCase: GetArtistsUseCase

    @InjectMockKs
    lateinit var commonViewModelDeps: CommonViewModelDeps

    @InjectMockKs
    lateinit var commonStateHolder: CommonStateHolder

    private lateinit var commonViewModel: CommonViewModel

    private val artistsFlow = MutableStateFlow<List<String>>(listOf())

    @Before
    fun initCommon() {
        mockkStatic(Log::class)

        val tagSlot = slot<String>()
        val msgSlot = slot<String>()

        every { Log.e(capture(tagSlot), capture(msgSlot)) } answers {
            val tag = tagSlot.captured
            val msg = msgSlot.captured
            println("$tag : $msg")
            0
        }

        every { getArtistsUseCase.execute() } returns artistsFlow

        every { toasts.showToast(anyInt()) } just runs
        every { toasts.showToast(anyString()) } just runs

        commonViewModel = CommonViewModel(commonStateHolder, commonViewModelDeps)

        verifySequence {
            settingsRepository.defaultArtist
            tvDetector.isTV
        }
    }

    @After
    fun clean() {
        unmockkStatic(Log::class)
    }

    @Test
    fun test001_selectScreen_SongList_isWorkingCorrect() {
        val artists = listOf("Первый", "Второй", "Третий")
        commonViewModel.selectScreen(CurrentScreenVariant.SONG_LIST)
        artistsFlow.value = artists
        assertEquals(CurrentScreenVariant.SONG_LIST, commonViewModel.currentScreenVariant.value)
        assertEquals(artists, commonViewModel.artistList.value)
        verifySequence {
            val defaultArtist = settingsRepository.defaultArtist
            Log.e("select screen", CurrentScreenVariant.SONG_LIST.toString())
            getArtistsUseCase.execute()
            val onArtistSelected = callbacks.onArtistSelected
            onArtistSelected(defaultArtist)
        }
    }

    @Test
    fun test002_selectScreen_Favorite_isWorkingCorrect() {
        val artists = listOf("Первый", "Второй", "Третий")
        commonViewModel.selectScreen(CurrentScreenVariant.FAVORITE)
        artistsFlow.value = artists
        assertEquals(CurrentScreenVariant.FAVORITE, commonViewModel.currentScreenVariant.value)
        assertEquals(artists, commonViewModel.artistList.value)
        verifySequence {
            Log.e("select screen", CurrentScreenVariant.FAVORITE.toString())
            getArtistsUseCase.execute()
            val onArtistSelected = callbacks.onArtistSelected
            onArtistSelected(ARTIST_FAVORITE)
        }
    }

    @Test
    fun test003_selectScreen_CloudSearch_isWorkingCorrect() {
        commonViewModel.selectScreen(CurrentScreenVariant.CLOUD_SEARCH, false)
        assertEquals(CurrentScreenVariant.CLOUD_SEARCH, commonViewModel.currentScreenVariant.value)
        commonViewModel.selectScreen(CurrentScreenVariant.CLOUD_SEARCH, true)
        verifySequence {
            Log.e("select screen", CurrentScreenVariant.CLOUD_SEARCH.toString())
            val onCloudSearchScreenSelected = callbacks.onCloudSearchScreenSelected
            onCloudSearchScreenSelected()
            Log.e("select screen", CurrentScreenVariant.CLOUD_SEARCH.toString())
        }
    }

    @Test
    fun test004_toasts_isWorkingCorrect() {
        commonViewModel.showToast(137)
        commonViewModel.showToast("Hello, world!")

        verifySequence {
            toasts.showToast(137)
            toasts.showToast("Hello, world!")
        }
    }

    @Test
    fun test005_setAppWasUpdated_isWorkingCorrect() {
        commonViewModel.setAppWasUpdated(true)
        assertEquals(commonViewModel.appWasUpdated.value, true)
        commonViewModel.setAppWasUpdated(false)
        assertEquals(commonViewModel.appWasUpdated.value, false)
    }
}