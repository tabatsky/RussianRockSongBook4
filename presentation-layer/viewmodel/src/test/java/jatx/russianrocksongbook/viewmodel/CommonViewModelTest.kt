package jatx.russianrocksongbook.viewmodel

import android.util.Log
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.junit5.MockKExtension
import jatx.russianrocksongbook.domain.repository.preferences.SettingsRepository
import jatx.russianrocksongbook.testutils.TestViewModelScopeRule
import jatx.russianrocksongbook.viewmodel.deps.Callbacks
import jatx.russianrocksongbook.viewmodel.deps.Resources
import jatx.russianrocksongbook.viewmodel.deps.TVDetector
import jatx.russianrocksongbook.viewmodel.deps.Toasts
import jatx.russianrocksongbook.navigation.ScreenVariant
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

        val screenVariantSlot = slot<ScreenVariant>()
        every { commonViewModel.selectScreen(capture(screenVariantSlot)) } answers {
            commonStateHolder.currentScreenVariant.value = screenVariantSlot.captured
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
        commonViewModel.selectScreen(ScreenVariant.CloudSearch(false))
        assertEquals(ScreenVariant.CloudSearch(false), commonViewModel.currentScreenVariant.value)
        commonViewModel.selectScreen(ScreenVariant.CloudSearch(true))
        assertEquals(ScreenVariant.CloudSearch(true), commonViewModel.currentScreenVariant.value)

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