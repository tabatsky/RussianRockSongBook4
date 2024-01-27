package jatx.russianrocksongbook.localsongs.internal.viewmodel

import android.util.Log
import io.mockk.*
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import jatx.russianrocksongbook.domain.models.local.Song
import jatx.russianrocksongbook.domain.repository.local.ARTIST_ADD_ARTIST
import jatx.russianrocksongbook.domain.repository.local.ARTIST_ADD_SONG
import jatx.russianrocksongbook.domain.repository.local.ARTIST_CLOUD_SONGS
import jatx.russianrocksongbook.domain.repository.local.ARTIST_DONATION
import jatx.russianrocksongbook.domain.usecase.cloud.AddSongToCloudUseCase
import jatx.russianrocksongbook.domain.usecase.local.*
import jatx.russianrocksongbook.localsongs.R
import jatx.russianrocksongbook.commonviewmodel.CommonViewModelTest
import jatx.russianrocksongbook.commonviewmodel.SelectScreen
import jatx.russianrocksongbook.commonviewmodel.ShowSongs
import jatx.russianrocksongbook.navigation.ScreenVariant
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters
import java.util.concurrent.TimeUnit

const val timeout = 500L

@MockKExtension.ConfirmVerification
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
open class LocalViewModelTest: CommonViewModelTest() {
    @RelaxedMockK
    lateinit var getArtistsUseCase: GetArtistsUseCase

    @RelaxedMockK
    lateinit var getSongsByArtistUseCase: GetSongsByArtistUseCase

    @RelaxedMockK
    lateinit var getCountByArtistUseCase: GetCountByArtistUseCase

    @RelaxedMockK
    lateinit var getSongByArtistAndPositionUseCase: GetSongByArtistAndPositionUseCase

    @RelaxedMockK
    lateinit var updateSongUseCase: UpdateSongUseCase

    @RelaxedMockK
    lateinit var deleteSongToTrashUseCase: DeleteSongToTrashUseCase

    @RelaxedMockK
    lateinit var addSongToCloudUseCase: AddSongToCloudUseCase

    internal lateinit var localViewModelDeps: LocalViewModelDeps

    internal lateinit var localStateHolder: LocalStateHolder

    private lateinit var localViewModel: LocalViewModel

    private val songList = listOf(
        Song(artist = "Кино", title = "title 1", text = "text text text"),
        Song(artist = "Кино", title = "title 2", text = "text text text"),
        Song(artist = "Кино", title = "title 3", text = "text text text"),
        Song(artist = "Кино", title = "title 4", text = "text text text"),
        Song(artist = "Кино", title = "title 5", text = "text text text")
    )

    private val artistsFlow = MutableStateFlow<List<String>>(listOf())

    private val songsFlow = MutableStateFlow<List<Song>>(listOf())

    private val songFlow = MutableStateFlow(songList[0])

    @Before
    fun initLocal() {
        localStateHolder = LocalStateHolder(commonStateHolder)
        localViewModelDeps = LocalViewModelDeps(
            commonViewModelDeps = commonViewModelDeps,
            getSongsByArtistUseCase = getSongsByArtistUseCase,
            getCountByArtistUseCase = getCountByArtistUseCase,
            getSongByArtistAndPositionUseCase = getSongByArtistAndPositionUseCase,
            updateSongUseCase = updateSongUseCase,
            deleteSongToTrashUseCase = deleteSongToTrashUseCase,
            addSongToCloudUseCase = addSongToCloudUseCase,
            getArtistsUseCase = getArtistsUseCase
        )

        localViewModel = LocalViewModel(
            localStateHolder = localStateHolder,
            localViewModelDeps = localViewModelDeps
        )

        every { getArtistsUseCase.execute() } returns artistsFlow
        every { getCountByArtistUseCase.execute(any()) } answers {
            songsFlow.value.size
        }
        every { getSongsByArtistUseCase.execute(any()) } returns songsFlow
        every { getSongByArtistAndPositionUseCase.execute(any(), any()) } returns songFlow
        every { updateSongUseCase.execute(any()) } just runs
        every { deleteSongToTrashUseCase.execute(any()) } just runs

        verifyAll {
            tvDetector.isTV
        }
    }

    @Test
    fun test001_selectScreen_SongList_isWorkingCorrect() {
        val artists = listOf("Первый", "Второй", "Третий")
        val defaultArtist = settingsRepository.defaultArtist
        localViewModel.submitAction(SelectScreen(ScreenVariant.SongList(defaultArtist)))
        localViewModel.submitAction(UpdateArtists)
        artistsFlow.value = artists
        assertEquals(ScreenVariant.SongList(defaultArtist), localViewModel.localState.value.currentScreenVariant)
        assertEquals(artists, localViewModel.localState.value.artistList)
        if (this is VoiceCommandViewModelTest) {
            verifySequence {
                settingsRepository.defaultArtist
                settingsRepository.theme
                settingsRepository.fontScaler
                settingsRepository.theme
                settingsRepository.fontScaler
                settingsRepository.theme
                settingsRepository.fontScaler
                settingsRepository.defaultArtist
                getArtistsUseCase.execute()
            }
        } else {
            verifySequence {
                settingsRepository.defaultArtist
                settingsRepository.theme
                settingsRepository.fontScaler
                settingsRepository.theme
                settingsRepository.fontScaler
                settingsRepository.defaultArtist
                getArtistsUseCase.execute()
            }
        }
    }

    @Test
    fun test002_selectScreen_Favorite_isWorkingCorrect() {
        val artists = listOf("Первый", "Второй", "Третий")
        localViewModel.submitAction(SelectScreen(ScreenVariant.Favorite()))
        localViewModel.submitAction(UpdateArtists)
        artistsFlow.value = artists
        assertEquals(ScreenVariant.Favorite(), localViewModel.localState.value.currentScreenVariant)
        assertEquals(artists, localViewModel.localState.value.artistList)
        verifySequence {
            getArtistsUseCase.execute()
        }
    }

    @Test
    fun test101_showSongs_Kino_withPassToSong_isWorkingCorrect() {
        songsFlow.value = songList
        localViewModel.submitAction(ShowSongs("Кино", "Кукушка"))

        TimeUnit.MILLISECONDS.sleep(timeout)

        assertEquals("Кино", localViewModel.localState.value.currentArtist)
        assertEquals(songList, localViewModel.localState.value.currentSongList)
        assertEquals(songList.size, localViewModel.localState.value.currentSongCount)

        verifySequence {
            Log.e("show songs", "Кино")
            getSongsByArtistUseCase.execute("Кино")
            Log.e("pass to song", "Кино - Кукушка")
        }
    }

    @Test
    fun test102_selectArtist_Kino_and_Alisa_withoutPassToSong_isWorkingCorrect() {
        val songList2 = listOf(
            Song(artist = "Кино", title = "title 6", text = "text text text"),
            Song(artist = "Кино", title = "title 7", text = "text text text"),
            Song(artist = "Кино", title = "title 8", text = "text text text"),
            Song(artist = "Кино", title = "title 9", text = "text text text"),
            Song(artist = "Кино", title = "title 10", text = "text text text")
        )
        val songList3 = listOf(
            Song(artist = "Алиса", title = "title 1", text = "text text text"),
            Song(artist = "Алиса", title = "title 2", text = "text text text"),
            Song(artist = "Алиса", title = "title 3", text = "text text text"),
            Song(artist = "Алиса", title = "title 4", text = "text text text"),
            Song(artist = "Алиса", title = "title 5", text = "text text text")
        )

        songsFlow.value = songList
        localViewModel.submitAction(SelectArtist("Кино"))

        TimeUnit.MILLISECONDS.sleep(timeout)
        assertEquals("Кино", localViewModel.localState.value.currentArtist)
        TimeUnit.MILLISECONDS.sleep(timeout)
        assertEquals(songList, localViewModel.localState.value.currentSongList)
        songsFlow.value = songList2
        TimeUnit.MILLISECONDS.sleep(timeout)
        assertEquals(songList2, localViewModel.localState.value.currentSongList)
        localViewModel.submitAction(SelectArtist("Алиса"))
        TimeUnit.MILLISECONDS.sleep(timeout)
        songsFlow.value = songList3
        TimeUnit.MILLISECONDS.sleep(timeout)
        assertEquals(songList3, localViewModel.localState.value.currentSongList)

        verifySequence {
            Log.e("select artist", "Кино")
            Log.e("show songs", "Кино")
            getCountByArtistUseCase.execute("Кино")
            getSongsByArtistUseCase.execute("Кино")
            Log.e("first song artist", "was: null; become: Кино")
            Log.e("select song", "0")
            Log.e("first song artist", "was: Кино; become: Кино")
            Log.e("select artist", "Алиса")
            Log.e("show songs", "Алиса")
            getCountByArtistUseCase.execute("Алиса")
            getSongsByArtistUseCase.execute("Алиса")
            Log.e("first song artist", "was: Кино; become: Алиса")
            Log.e("select song", "0")
        }
    }

    @Test
    fun test103_selectArtist_addArtist_isWorkingCorrect() {
        localViewModel.submitAction(SelectArtist(ARTIST_ADD_ARTIST))

        assertEquals(ScreenVariant.AddArtist, localViewModel.localState.value.currentScreenVariant)

        verifySequence {
            Log.e("select artist", ARTIST_ADD_ARTIST)
            Log.e("navigate", "AddArtist")
        }
    }

    @Test
    fun test104_selectArtist_addSong_isWorkingCorrect() {
        localViewModel.submitAction(SelectArtist(ARTIST_ADD_SONG))

        assertEquals(ScreenVariant.AddSong, localViewModel.localState.value.currentScreenVariant)

        verifySequence {
            Log.e("select artist", ARTIST_ADD_SONG)
            Log.e("navigate", "AddSong")
        }
    }

    @Test
    fun test105_selectArtist_cloudSongs_isWorkingCorrect() {
        localViewModel.submitAction(SelectArtist(ARTIST_CLOUD_SONGS))

        assertTrue(localViewModel.localState.value.currentScreenVariant is ScreenVariant.CloudSearch)

        val destination = localViewModel.localState.value.currentScreenVariant.destination

        verifySequence {
            Log.e("select artist", ARTIST_CLOUD_SONGS)
            Log.e("navigate", destination)
        }
    }

    @Test
    fun test106_selectArtist_Donation_isWorkingCorrect() {
        localViewModel.submitAction(SelectArtist(ARTIST_DONATION))

        assertEquals(ScreenVariant.Donation, localViewModel.localState.value.currentScreenVariant)

        verifySequence {
            Log.e("select artist", ARTIST_DONATION)
            Log.e("navigate", "Donation")
        }
    }

    @Test
    fun test107_selectSong_isWorkingCorrect() {
        songsFlow.value = songList

        TimeUnit.MILLISECONDS.sleep(timeout)

        localViewModel.submitAction(SelectArtist("Кино"))

        TimeUnit.MILLISECONDS.sleep(timeout)

        localViewModel.submitAction(SelectSong(13))

        TimeUnit.MILLISECONDS.sleep(timeout)

        assertEquals(13, localViewModel.localState.value.songListScrollPosition)
        assertEquals(true, localViewModel.localState.value.songListNeedScroll)
        assertEquals(songList[0], localViewModel.localState.value.currentSong)
        assertEquals(13, localViewModel.localState.value.currentSongPosition)

        verifySequence {
            Log.e("select artist", "Кино")
            Log.e("show songs", "Кино")
            getCountByArtistUseCase.execute("Кино")
            getSongsByArtistUseCase.execute("Кино")
            Log.e("first song artist", "was: null; become: Кино")
            Log.e("select song", "0")
            getSongByArtistAndPositionUseCase.execute("Кино", 0)
            Log.e("select song", "13")
            getSongByArtistAndPositionUseCase.execute("Кино", 13)
        }
    }

    @Test
    fun test108_setFavorite_isWorkingCorrect() {
        localViewModel.submitAction(SelectSong(13))

        TimeUnit.MILLISECONDS.sleep(timeout)

        localViewModel.submitAction(SetFavorite(true))
        TimeUnit.MILLISECONDS.sleep(timeout)
        assertEquals(true, localViewModel.localState.value.currentSong?.favorite)
        val song1 = localViewModel.localState.value.currentSong ?: throw IllegalStateException()

        localViewModel.submitAction(SetFavorite(false))
        TimeUnit.MILLISECONDS.sleep(timeout)
        assertEquals(false, localViewModel.localState.value.currentSong?.favorite)
        val song2 = localViewModel.localState.value.currentSong ?: throw IllegalStateException()

        verifySequence {
            Log.e("select song", "13")
            getSongByArtistAndPositionUseCase.execute("", 13)
            Log.e("set favorite", "true")
            updateSongUseCase.execute(song1)
            toasts.showToast(R.string.toast_added_to_favorite)
            Log.e("set favorite", "false")
            updateSongUseCase.execute(song2)
            toasts.showToast(R.string.toast_removed_from_favorite)
        }
    }

    @Test
    fun test109_deleteCurrentToTrash_isWorkingCorrect() {
        localViewModel.submitAction(SelectSong(13))

        TimeUnit.MILLISECONDS.sleep(timeout)

        val song = localViewModel.localState.value.currentSong ?: throw IllegalStateException()

        localViewModel.submitAction(DeleteCurrentToTrash)
        TimeUnit.MILLISECONDS.sleep(timeout)

        verifySequence {
            Log.e("select song", "13")
            getSongByArtistAndPositionUseCase.execute("", 13)
            deleteSongToTrashUseCase.execute(song)
            Log.e("back by", "user")
            toasts.showToast(R.string.toast_deleted_to_trash)
        }
    }
}