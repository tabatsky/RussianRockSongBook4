package jatx.russianrocksongbook.localsongs.internal.viewmodel

import android.util.Log
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.spyk
import io.mockk.verifySequence
import jatx.russianrocksongbook.domain.models.local.Song
import jatx.russianrocksongbook.domain.repository.local.ARTIST_ADD_ARTIST
import jatx.russianrocksongbook.domain.repository.local.ARTIST_ADD_SONG
import jatx.russianrocksongbook.domain.repository.local.ARTIST_CLOUD_SONGS
import jatx.russianrocksongbook.domain.repository.local.ARTIST_DONATION
import jatx.russianrocksongbook.domain.usecase.local.GetArtistsAsListUseCase
import jatx.russianrocksongbook.domain.usecase.local.GetSongsByVoiceSearchUseCase
import jatx.russianrocksongbook.localsongs.R
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters
import java.util.concurrent.TimeUnit

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
        voiceCommandViewModelDeps = VoiceCommandViewModelDeps(
            localViewModelDeps = localViewModelDeps,
            getArtistsAsListUseCase = getArtistsAsListUseCase,
            getSongsByVoiceSearchUseCase = getSongsByVoiceSearchUseCase
        )
        val _voiceCommandViewModel = VoiceCommandViewModel(
            localStateHolder = localStateHolder,
            voiceCommandViewModelDeps = voiceCommandViewModelDeps
        )
        voiceCommandViewModel = spyk(_voiceCommandViewModel)

        every { voiceCommandViewModel.selectArtist(any()) } answers {
            if (arg(0) in listOf(
                    ARTIST_ADD_ARTIST,
                    ARTIST_ADD_SONG,
                    ARTIST_CLOUD_SONGS,
                    ARTIST_DONATION
                )
            ) {
                _voiceCommandViewModel.selectArtist(arg(0))
            } else {
                _voiceCommandViewModel.showSongs(arg(0), null)
            }
        }
        every { getArtistsAsListUseCase.execute() } returns artistList
    }

    @Test
    fun test201_parseAndExecuteVoiceCommand_existingGroup_isWorkingCorrect() {
        voiceCommandViewModel.parseAndExecuteVoiceCommand("открой группу немного нервно")

        TimeUnit.MILLISECONDS.sleep(200)

        assertEquals("Немного Нервно", voiceCommandViewModel.currentArtist.value)

        verifySequence {
            Log.e("voice command", "открой группу немного нервно")
            Log.e("show songs", "Немного Нервно")
            getCountByArtistUseCase.execute("Немного Нервно")
            getSongsByArtistUseCase.execute("Немного Нервно")
        }
    }

    @Test
    fun test202_parseAndExecuteVoiceCommand_notExistingGroup_isWorkingCorrect() {
        voiceCommandViewModel.parseAndExecuteVoiceCommand("открой группу кино")

        TimeUnit.MILLISECONDS.sleep(200)

        verifySequence {
            Log.e("voice command", "открой группу кино")
            toasts.showToast(R.string.toast_artist_not_found)
        }
    }

    @Test
    fun test203_parseAndExecuteVoiceCommand_existingSong_isWorkingCorrect() {
        every { getSongsByVoiceSearchUseCase.execute(any()) } returns listOf(
            Song(artist = "Кино", title = "Группа крови", text = "Бла-бла-бла")
        )

        voiceCommandViewModel.parseAndExecuteVoiceCommand("открой песню кино группа крови")

        TimeUnit.MILLISECONDS.sleep(200)

        verifySequence {
            Log.e("voice command", "открой песню кино группа крови")
            getSongsByVoiceSearchUseCase.execute("киногруппакрови")
            callbacks.onSongByArtistAndTitleSelected("Кино", "Группа крови")
        }
    }

    @Test
    fun test204_parseAndExecuteVoiceCommand_notExistingSong_isWorkingCorrect() {
        every { getSongsByVoiceSearchUseCase.execute(any()) } returns listOf()

        voiceCommandViewModel.parseAndExecuteVoiceCommand("открой песню весна космос")

        TimeUnit.MILLISECONDS.sleep(200)

        verifySequence {
            Log.e("voice command", "открой песню весна космос")
            getSongsByVoiceSearchUseCase.execute("веснакосмос")
            toasts.showToast(R.string.toast_song_not_found)
        }
    }

    @Test
    fun test205_parseAndExecuteVoiceCommand_unknownCommand_isWorkingCorrect() {
        voiceCommandViewModel.parseAndExecuteVoiceCommand("раз два три")

        TimeUnit.MILLISECONDS.sleep(200)

        verifySequence {
            Log.e("voice command", "раз два три")
            toasts.showToast(R.string.toast_unknown_command)
        }
    }
}