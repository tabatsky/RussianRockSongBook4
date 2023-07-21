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
import jatx.russianrocksongbook.domain.usecase.cloud.AddWarningLocalUseCase
import jatx.russianrocksongbook.domain.usecase.local.*
import jatx.russianrocksongbook.localsongs.R
import jatx.russianrocksongbook.commonviewmodel.CommonViewModelTest
import jatx.russianrocksongbook.navigation.ScreenVariant
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters
import java.util.concurrent.TimeUnit

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
    lateinit var addWarningLocalUseCase: AddWarningLocalUseCase

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
            addWarningLocalUseCase = addWarningLocalUseCase,
            addSongToCloudUseCase = addSongToCloudUseCase,
            getArtistsUseCase = getArtistsUseCase
        )
        val __localViewModel = LocalViewModel(
            localStateHolder = localStateHolder,
            localViewModelDeps = localViewModelDeps
        )
        val _localViewModel = spyk(__localViewModel)

        val screenVariantSlot = slot<ScreenVariant>()
        every { _localViewModel.selectScreen(capture(screenVariantSlot)) } answers {
            commonStateHolder.currentScreenVariant.value = screenVariantSlot.captured
        }

        localViewModel = spyk(_localViewModel)
        every { localViewModel.selectArtist(any()) } answers {
            if (arg(0) in listOf(
                    ARTIST_ADD_ARTIST,
                    ARTIST_ADD_SONG,
                    ARTIST_CLOUD_SONGS,
                    ARTIST_DONATION)
            ) {
                _localViewModel.selectArtist(arg(0))
            } else {
                _localViewModel.showSongs(arg(0), null)
            }
        }
        every { localViewModel.selectScreen(any()) } answers {
            _localViewModel.selectScreen(arg(0))
        }

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
        localViewModel.selectScreen(ScreenVariant.SongList(defaultArtist))
        localViewModel.updateArtists()
        artistsFlow.value = artists
        assertEquals(ScreenVariant.SongList(defaultArtist), localViewModel.currentScreenVariant.value)
        assertEquals(artists, localViewModel.artistList.value)
        verifySequence {
            settingsRepository.defaultArtist
            settingsRepository.defaultArtist
            getArtistsUseCase.execute()
        }
    }

    @Test
    fun test002_selectScreen_Favorite_isWorkingCorrect() {
        val artists = listOf("Первый", "Второй", "Третий")
        localViewModel.selectScreen(ScreenVariant.Favorite())
        localViewModel.updateArtists()
        artistsFlow.value = artists
        assertEquals(ScreenVariant.Favorite(), localViewModel.currentScreenVariant.value)
        assertEquals(artists, localViewModel.artistList.value)
        verifySequence {
            getArtistsUseCase.execute()
        }
    }

    @Test
    fun test101_showSongs_Kino_withPassToSong_isWorkingCorrect() {
        songsFlow.value = songList
        localViewModel.showSongs("Кино", "Кукушка")

        TimeUnit.MILLISECONDS.sleep(200)

        assertEquals("Кино", localViewModel.currentArtist.value)
        assertEquals(songList, localViewModel.currentSongList.value)
        assertEquals(songList.size, localViewModel.currentSongCount.value)

        verifySequence {
            Log.e("show songs", "Кино")
            getCountByArtistUseCase.execute("Кино")
            getSongsByArtistUseCase.execute("Кино")
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
        localViewModel.selectArtist("Кино")

        assertEquals("Кино", localViewModel.currentArtist.value)
        TimeUnit.MILLISECONDS.sleep(200)
        assertEquals(songList, localViewModel.currentSongList.value)
        songsFlow.value = songList2
        TimeUnit.MILLISECONDS.sleep(200)
        assertEquals(songList2, localViewModel.currentSongList.value)
        localViewModel.selectArtist("Алиса")
        TimeUnit.MILLISECONDS.sleep(200)
        songsFlow.value = songList3
        TimeUnit.MILLISECONDS.sleep(200)
        assertEquals(songList3, localViewModel.currentSongList.value)

        verifySequence {
            Log.e("show songs", "Кино")
            getCountByArtistUseCase.execute("Кино")
            getSongsByArtistUseCase.execute("Кино")
            Log.e("artists", "null Кино")
            Log.e("select song", "0")
            Log.e("show songs", "Алиса")
            getCountByArtistUseCase.execute("Алиса")
            getSongsByArtistUseCase.execute("Алиса")
            Log.e("artists", "Кино Алиса")
            Log.e("select song", "0")
        }
    }

    @Test
    fun test103_selectArtist_addArtist_isWorkingCorrect() {
        localViewModel.selectArtist(ARTIST_ADD_ARTIST)

        assertEquals(ScreenVariant.AddArtist, localViewModel.currentScreenVariant.value)

        verifySequence {
            Log.e("select artist", ARTIST_ADD_ARTIST)
        }
    }

    @Test
    fun test104_selectArtist_addSong_isWorkingCorrect() {
        localViewModel.selectArtist(ARTIST_ADD_SONG)

        assertEquals(ScreenVariant.AddSong, localViewModel.currentScreenVariant.value)

        verifySequence {
            Log.e("select artist", ARTIST_ADD_SONG)
        }
    }

    @Test
    fun test105_selectArtist_cloudSongs_isWorkingCorrect() {
        localViewModel.selectArtist(ARTIST_CLOUD_SONGS)

        assertTrue(localViewModel.currentScreenVariant.value is ScreenVariant.CloudSearch)

        verifySequence {
            Log.e("select artist", ARTIST_CLOUD_SONGS)
        }
    }

    @Test
    fun test106_selectArtist_Donation_isWorkingCorrect() {
        localViewModel.selectArtist(ARTIST_DONATION)

        assertEquals(ScreenVariant.Donation, localViewModel.currentScreenVariant.value)

        verifySequence {
            Log.e("select artist", ARTIST_DONATION)
        }
    }

    @Test
    fun test107_selectSong_isWorkingCorrect() {
        songsFlow.value = songList
        localViewModel.selectArtist("Кино")

        TimeUnit.MILLISECONDS.sleep(200)

        localViewModel.selectSong(13)

        TimeUnit.MILLISECONDS.sleep(200)

        assertEquals(13, localViewModel.scrollPosition.value)
        assertEquals(true, localViewModel.needScroll.value)
        assertEquals(songList[0], localViewModel.currentSong.value)
        assertEquals(13, localViewModel.currentSongPosition.value)

        verifySequence {
            Log.e("show songs", "Кино")
            getCountByArtistUseCase.execute("Кино")
            getSongsByArtistUseCase.execute("Кино")
            Log.e("artists", "null Кино")
            Log.e("select song", "0")
            getSongByArtistAndPositionUseCase.execute("Кино", 0)
            Log.e("select song", "13")
            getSongByArtistAndPositionUseCase.execute("Кино", 13)
        }
    }

    @Test
    fun test108_setFavorite_isWorkingCorrect() {
        localViewModel.selectSong(13)

        TimeUnit.MILLISECONDS.sleep(200)

        localViewModel.setFavorite(true)
        TimeUnit.MILLISECONDS.sleep(200)
        assertEquals(true, localViewModel.currentSong.value?.favorite)
        val song1 = localViewModel.currentSong.value ?: throw IllegalStateException()

        localViewModel.setFavorite(false)
        TimeUnit.MILLISECONDS.sleep(200)
        assertEquals(false, localViewModel.currentSong.value?.favorite)
        val song2 = localViewModel.currentSong.value ?: throw IllegalStateException()

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
        localViewModel.selectSong(13)

        TimeUnit.MILLISECONDS.sleep(200)

        val song = localViewModel.currentSong.value ?: throw IllegalStateException()

        localViewModel.deleteCurrentToTrash()
        TimeUnit.MILLISECONDS.sleep(200)

        verifySequence {
            Log.e("select song", "13")
            getSongByArtistAndPositionUseCase.execute("", 13)
            deleteSongToTrashUseCase.execute(song)
            Log.e("back from", ScreenVariant.Start.toString())
            toasts.showToast(R.string.toast_deleted_to_trash)
        }
    }
}