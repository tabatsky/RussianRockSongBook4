package jatx.russianrocksongbook.localsongs.internal.viewmodel

import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verifyAll
import jatx.russianrocksongbook.domain.models.local.Song
import jatx.russianrocksongbook.domain.usecase.local.GetArtistsAsListUseCase
import jatx.russianrocksongbook.domain.usecase.local.GetSongsByVoiceSearchUseCase
import jatx.russianrocksongbook.localsongs.R
import jatx.russianrocksongbook.commonviewmodel.waitForCondition
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters

@MockKExtension.ConfirmVerification
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class VoiceCommandViewModelTest: LocalViewModelTest() {

    @RelaxedMockK
    lateinit var getArtistsAsListUseCase: GetArtistsAsListUseCase

    @RelaxedMockK
    lateinit var getSongsByVoiceSearchUseCase: GetSongsByVoiceSearchUseCase

    private lateinit var voiceCommandViewModelDeps: VoiceCommandViewModelDeps

    private lateinit var voiceCommandViewModel: VoiceCommandViewModel

    private val artistList = listOf("Александр Башлачёв", "Сплин", "Немного Нервно")

    @Before
    fun initVoiceCommand() {
        initLocal()

        voiceCommandViewModelDeps = VoiceCommandViewModelDeps(
            localViewModelDeps = localViewModelDeps,
            getArtistsAsListUseCase = getArtistsAsListUseCase,
            getSongsByVoiceSearchUseCase = getSongsByVoiceSearchUseCase
        )

        voiceCommandViewModel = VoiceCommandViewModel(
            localStateHolder = localStateHolder,
            voiceCommandViewModelDeps = voiceCommandViewModelDeps
        )
        voiceCommandViewModel.launchJobsIfNecessary()

        every { getArtistsAsListUseCase.execute() } returns artistList
    }

    @Test
    fun test201_parseAndExecuteVoiceCommand_existingGroup_isWorkingCorrect() {
        voiceCommandViewModel.submitAction(
            (ParseAndExecuteVoiceCommand("открой группу немного нервно")))

        waitForCondition {
            voiceCommandViewModel.appStateFlow.value.currentArtist == "Немного Нервно"
        }
        assertEquals("Немного Нервно", voiceCommandViewModel.appStateFlow.value.currentArtist)
    }

    @Test
    fun test202_parseAndExecuteVoiceCommand_notExistingGroup_isWorkingCorrect() {
        voiceCommandViewModel.submitAction(
            (ParseAndExecuteVoiceCommand("открой группу кино")))

        verifyAll {
            toasts.showToast(R.string.toast_artist_not_found)
        }
    }

    @Test
    fun test203_parseAndExecuteVoiceCommand_existingSong_isWorkingCorrect() {
        every { getSongsByVoiceSearchUseCase.execute(any()) } returns listOf(
            Song(artist = "Кино", title = "Группа крови", text = "Бла-бла-бла")
        )

        voiceCommandViewModel.submitAction(
            (ParseAndExecuteVoiceCommand("открой песню кино группа крови")))

        verifyAll{
            callbacks.onSongByArtistAndTitleSelected("Кино", "Группа крови")
        }
    }

    @Test
    fun test204_parseAndExecuteVoiceCommand_notExistingSong_isWorkingCorrect() {
        every { getSongsByVoiceSearchUseCase.execute(any()) } returns listOf()

        voiceCommandViewModel.submitAction(
            (ParseAndExecuteVoiceCommand("открой песню весна космос")))

        verifyAll {
            toasts.showToast(R.string.toast_song_not_found)
        }
    }

    @Test
    fun test205_parseAndExecuteVoiceCommand_unknownCommand_isWorkingCorrect() {
        voiceCommandViewModel.submitAction(
            (ParseAndExecuteVoiceCommand("раз два три")))

        verifyAll {
            toasts.showToast(R.string.toast_unknown_command)
        }
    }
}