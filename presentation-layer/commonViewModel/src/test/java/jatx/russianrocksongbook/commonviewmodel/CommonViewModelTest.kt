package jatx.russianrocksongbook.commonviewmodel

import android.util.Log
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
import jatx.russianrocksongbook.navigation.ScreenVariant
import kotlinx.coroutines.flow.update
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

    @InjectMockKs
    lateinit var commonViewModelDeps: CommonViewModelDeps

    @InjectMockKs
    lateinit var commonStateHolder: CommonStateHolder

    private lateinit var commonViewModel: CommonViewModel

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


        every { toasts.showToast(anyInt()) } just runs
        every { toasts.showToast(anyString()) } just runs

        val _commonViewModel = CommonViewModel(commonStateHolder, commonViewModelDeps)
        commonViewModel = spyk(_commonViewModel)

        val actionSlot = slot<UIAction>()
        every { commonViewModel.submitAction(capture(actionSlot)) } answers {
            val action = actionSlot.captured
            when (action) {
                is SelectScreen -> {
                    commonStateHolder.commonState.update {
                        it.copy(currentScreenVariant = action.screenVariant)
                    }
                }
                is AppWasUpdated -> {
                    commonStateHolder.commonState.update {
                        it.copy(appWasUpdated = action.wasUpdated)
                    }
                }
            }
        }

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
    fun test003_selectScreen_CloudSearch_isWorkingCorrect() {
        commonViewModel.submitAction(SelectScreen(ScreenVariant.CloudSearch(false)))
        assertEquals(ScreenVariant.CloudSearch(false), commonViewModel.commonState.value.currentScreenVariant)
        commonViewModel.submitAction(SelectScreen(ScreenVariant.CloudSearch(true)))
        assertEquals(ScreenVariant.CloudSearch(true), commonViewModel.commonState.value.currentScreenVariant)

    }

    @Test
    fun test004_toasts_isWorkingCorrect() {
        commonViewModel.submitEffect(ShowToastWithResource(137))
        commonViewModel.submitEffect(ShowToastWithText("Hello, world!"))

        verifySequence {
            toasts.showToast(137)
            toasts.showToast("Hello, world!")
        }
    }

    @Test
    fun test005_setAppWasUpdated_isWorkingCorrect() {
        commonViewModel.submitAction(AppWasUpdated(true))
        assertEquals(commonViewModel.commonState.value.appWasUpdated, true)
        commonViewModel.submitAction(AppWasUpdated(false))
        assertEquals(commonViewModel.commonState.value.appWasUpdated, false)
    }
}