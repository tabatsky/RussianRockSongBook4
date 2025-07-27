package jatx.russianrocksongbook.commonviewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.navigation3.runtime.NavKey
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.junit5.MockKExtension
import jatx.russianrocksongbook.domain.repository.preferences.SettingsRepository
import jatx.russianrocksongbook.testutils.TestViewModelScopeRule
import jatx.russianrocksongbook.commonviewmodel.deps.Callbacks
import jatx.russianrocksongbook.commonviewmodel.deps.Resources
import jatx.russianrocksongbook.commonviewmodel.deps.TVDetector
import jatx.russianrocksongbook.commonviewmodel.deps.Toasts
import jatx.russianrocksongbook.domain.repository.cloud.result.ResultWithoutData
import jatx.russianrocksongbook.domain.repository.cloud.result.STATUS_SUCCESS
import jatx.russianrocksongbook.domain.usecase.cloud.AddSongToCloudUseCase
import jatx.russianrocksongbook.domain.usecase.cloud.AddWarningUseCase
import jatx.russianrocksongbook.navigation.*
import jatx.russianrocksongbook.testing.TestingConfig
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.runners.MethodSorters
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import java.util.concurrent.TimeUnit

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
    lateinit var addWarningUseCase: AddWarningUseCase

    @RelaxedMockK
    lateinit var addSongToCloudUseCase: AddSongToCloudUseCase

    @InjectMockKs
    lateinit var commonViewModelDeps: CommonViewModelDeps

    @InjectMockKs
    lateinit var appStateHolder: AppStateHolder

    private lateinit var commonViewModel: CommonViewModel

    @Before
    fun initCommon() {
        TestingConfig.isUnitTesting = true

        mockkStatic(Log::class)

        val tagSlot = slot<String>()
        val msgSlot = slot<String>()

        every { Log.e(capture(tagSlot), capture(msgSlot)) } answers {
            val tag = tagSlot.captured
            val msg = msgSlot.captured
            println("$tag : $msg")
            0
        }

        val backStack = mutableStateListOf<NavKey>(EmptyScreenVariant, StartScreenVariant)

        every { toasts.showToast(anyInt()) } just runs
        every { toasts.showToast(anyString()) } just runs

        every { callbacks.onOpenYandexMusic(anyString()) } just runs
        every { callbacks.onOpenYoutubeMusic(anyString()) } just runs
        every { callbacks.onOpenVkMusic(anyString()) } just runs
        every { callbacks.onReviewApp() } just runs
        every { callbacks.onShowDevSite() } just runs

        coEvery { addWarningUseCase.execute(any(), anyString()) } returns
                ResultWithoutData(STATUS_SUCCESS, null)

        val _commonViewModel = CommonViewModel(appStateHolder, commonViewModelDeps)
        _commonViewModel.injectBackStack(backStack)
        _commonViewModel.launchJobsIfNecessary()
        commonViewModel = spyk(_commonViewModel)

        val actionSlot = slot<UIAction>()
        every { commonViewModel.submitAction(capture(actionSlot)) } answers {
            when (val action = actionSlot.captured) {
                is SelectScreen -> {
                    val screenVariant = action.screenVariant
                    println(screenVariant)
                    val appState = appStateHolder.appStateFlow.value
                    val newState = if (screenVariant is SongListScreenVariant) {
                        appState.copy(
                            currentArtist = screenVariant.artist
                        )
                    } else {
                        appState
                    }
                    appStateHolder.changeAppState(newState)
                    _commonViewModel.submitAction(action)
                }
                else -> {
                    _commonViewModel.submitAction(action)
                }
            }
        }
    }

    @After
    fun clean() {
        unmockkStatic(Log::class)
    }

    @Test
    fun test003_selectScreen_CloudSearch_withBackPressing_isWorkingCorrect() {
        commonViewModel.submitAction(SelectScreen(SongListScreenVariant("Кино")))
        assertEquals(SongListScreenVariant("Кино"), commonViewModel.appStateFlow.value.currentScreenVariant)
        commonViewModel.submitAction(SelectScreen(CloudSearchScreenVariant(137,true)))
        assertEquals(CloudSearchScreenVariant(137, true), commonViewModel.appStateFlow.value.currentScreenVariant)
        commonViewModel.submitAction(Back(false))
        waitForCondition {
            commonViewModel.appStateFlow.value.currentScreenVariant is SongListScreenVariant
        }
        assertEquals(SongListScreenVariant("Кино", isBackFromSomeScreen = true), commonViewModel.appStateFlow.value.currentScreenVariant)
    }

    @Test
    fun test004_selectScreen_CloudSongText_withBackPressing_isWorkingCorrect() {
        commonViewModel.submitAction(SelectScreen(SongListScreenVariant("Кино")))
        assertEquals(SongListScreenVariant("Кино"), commonViewModel.appStateFlow.value.currentScreenVariant)
        commonViewModel.submitAction(SelectScreen(CloudSearchScreenVariant(137,false)))
        assertEquals(CloudSearchScreenVariant(137,false), commonViewModel.appStateFlow.value.currentScreenVariant)
        commonViewModel.submitAction(SelectScreen(CloudSongTextScreenVariant(13)))
        assertEquals(CloudSongTextScreenVariant(13), commonViewModel.appStateFlow.value.currentScreenVariant)
        commonViewModel.submitAction(Back(false))
        waitForCondition {
            commonViewModel.appStateFlow.value.currentScreenVariant is CloudSearchScreenVariant
        }
        assertEquals(CloudSearchScreenVariant(137,true), commonViewModel.appStateFlow.value.currentScreenVariant)
    }

    @Test
    fun test005_selectScreen_SongText_withBackPressing_isWorkingCorrect() {
        commonViewModel.submitAction(SelectScreen(SongListScreenVariant("Кино")))
        assertEquals(SongListScreenVariant("Кино"), commonViewModel.appStateFlow.value.currentScreenVariant)
        commonViewModel.submitAction(SelectScreen(SongTextScreenVariant(13, 1237)))
        assertEquals(SongTextScreenVariant(13, 1237), commonViewModel.appStateFlow.value.currentScreenVariant)
        commonViewModel.submitAction(Back(false))
        waitForCondition {
            commonViewModel.appStateFlow.value.currentScreenVariant is SongListScreenVariant
        }
        assertEquals(SongListScreenVariant("Кино", isBackFromSomeScreen = true), commonViewModel.appStateFlow.value.currentScreenVariant)
    }

    @Test
    fun test006_selectScreen_Settings_withBackPressing_isWorkingCorrect() {
        commonViewModel.submitAction(SelectScreen(SongListScreenVariant("Кино")))
        assertEquals(SongListScreenVariant("Кино"), commonViewModel.appStateFlow.value.currentScreenVariant)
        commonViewModel.submitAction(SelectScreen(SettingsScreenVariant))
        assertEquals(SettingsScreenVariant, commonViewModel.appStateFlow.value.currentScreenVariant)
        commonViewModel.submitAction(Back(false))
        waitForCondition {
            commonViewModel.appStateFlow.value.currentScreenVariant is SongListScreenVariant
        }
        assertEquals(SongListScreenVariant("Кино", isBackFromSomeScreen = true), commonViewModel.appStateFlow.value.currentScreenVariant)
    }

    @Test
    fun test020_toasts_isWorkingCorrect() {
        commonViewModel.submitEffect(ShowToastWithResource(137))
        commonViewModel.submitEffect(ShowToastWithText("Hello, world!"))

        verifySequence {
            toasts.showToast(137)
            toasts.showToast("Hello, world!")
        }
    }

    @Test
    fun test030_setAppWasUpdated_isWorkingCorrect() {
        commonViewModel.submitAction(AppWasUpdated(true))
        assertEquals(commonViewModel.appStateFlow.value.appWasUpdated, true)
        commonViewModel.submitAction(AppWasUpdated(false))
        assertEquals(commonViewModel.appStateFlow.value.appWasUpdated, false)
    }
}

fun waitForCondition(condition: () -> Boolean) {
    var counter = 0
    while (!condition() && counter < 1000) {
        TimeUnit.MILLISECONDS.sleep(10)
        counter++
    }
    assertTrue(condition())
}