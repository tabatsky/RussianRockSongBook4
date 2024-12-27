package jatx.russianrocksongbook.localsongs.internal.viewmodel

import io.mockk.*
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import jatx.russianrocksongbook.commonsongtext.viewmodel.CommonSongTextStateHolder
import jatx.russianrocksongbook.commonsongtext.viewmodel.CommonSongTextViewModelDeps
import jatx.russianrocksongbook.domain.models.local.Song
import jatx.russianrocksongbook.domain.repository.local.ARTIST_ADD_ARTIST
import jatx.russianrocksongbook.domain.repository.local.ARTIST_ADD_SONG
import jatx.russianrocksongbook.domain.repository.local.ARTIST_CLOUD_SONGS
import jatx.russianrocksongbook.domain.repository.local.ARTIST_DONATION
import jatx.russianrocksongbook.domain.usecase.local.*
import jatx.russianrocksongbook.localsongs.R
import jatx.russianrocksongbook.commonviewmodel.CommonViewModelTest
import jatx.russianrocksongbook.commonviewmodel.OpenVkMusic
import jatx.russianrocksongbook.commonviewmodel.OpenYandexMusic
import jatx.russianrocksongbook.commonviewmodel.OpenYoutubeMusic
import jatx.russianrocksongbook.commonviewmodel.SelectScreen
import jatx.russianrocksongbook.commonviewmodel.SendWarning
import jatx.russianrocksongbook.commonviewmodel.ShowSongs
import jatx.russianrocksongbook.commonviewmodel.waitForCondition
import jatx.russianrocksongbook.commonsongtext.viewmodel.NextSong
import jatx.russianrocksongbook.commonsongtext.viewmodel.PrevSong
import jatx.russianrocksongbook.commonsongtext.viewmodel.SelectSong
import jatx.russianrocksongbook.commonsongtext.viewmodel.SetAutoPlayMode
import jatx.russianrocksongbook.commonsongtext.viewmodel.SetEditorMode
import jatx.russianrocksongbook.commonsongtext.viewmodel.SetFavorite
import jatx.russianrocksongbook.commonsongtext.viewmodel.UpdateCurrentSong
import jatx.russianrocksongbook.commonsongtext.viewmodel.UpdateSongListNeedScroll
import jatx.russianrocksongbook.commonsongtext.viewmodel.UpdateSongListScrollPosition
import jatx.russianrocksongbook.commonview.viewmodel.DeleteCurrentToTrash
import jatx.russianrocksongbook.domain.repository.local.ARTIST_FAVORITE
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

    internal lateinit var commonSongTextViewModelDeps: CommonSongTextViewModelDeps

    internal lateinit var localViewModelDeps: LocalViewModelDeps

    internal lateinit var commonSongTextStateHolder: CommonSongTextStateHolder

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

    private var theSongs = listOf<Song>()

    private var theSong = songList[0]

    @Before
    fun initLocal() {
        initCommon()

        commonSongTextStateHolder = CommonSongTextStateHolder(appStateHolder)
        localStateHolder = LocalStateHolder(commonSongTextStateHolder)
        commonSongTextViewModelDeps = CommonSongTextViewModelDeps(
            commonViewModelDeps = commonViewModelDeps,
            getCountByArtistUseCase = getCountByArtistUseCase,
            getSongByArtistAndPositionUseCase = getSongByArtistAndPositionUseCase,
            updateSongUseCase = updateSongUseCase,
            deleteSongToTrashUseCase = deleteSongToTrashUseCase
        )
        localViewModelDeps = LocalViewModelDeps(
            commonSongTextViewModelDeps = commonSongTextViewModelDeps,
            getSongsByArtistUseCase = getSongsByArtistUseCase,
            getArtistsUseCase = getArtistsUseCase
        )

        localViewModel = LocalViewModel(
            localStateHolder = localStateHolder,
            localViewModelDeps = localViewModelDeps
        )
        localViewModel.launchJobsIfNecessary()

        every { getArtistsUseCase.execute() } returns artistsFlow
        every { getCountByArtistUseCase.execute(any()) } answers {
            theSongs.size
        }
        coEvery { getSongsByArtistUseCase.execute(any()) } answers {
            theSongs
        }
        coEvery { getSongByArtistAndPositionUseCase.execute(any(), any()) } answers {
            theSong
        }

        val songSlot = slot<Song>()
        every { updateSongUseCase.execute(capture(songSlot)) } answers {
            val song = songSlot.captured

            theSong = song

            if (appStateHolder.appStateFlow.value.currentArtist == ARTIST_FAVORITE &&
                !song.favorite) {

                val songs = theSongs
                theSongs = songs.filter {
                    it.artist != song.artist || it.title != song.title
                }
            }
        }
        every { deleteSongToTrashUseCase.execute(any()) } just runs
    }

    @Test
    fun test001_selectScreen_SongList_isWorkingCorrect() {
        val artists = listOf("Первый", "Второй", "Третий")
        val defaultArtist = settingsRepository.defaultArtist
        localViewModel.submitAction(SelectScreen(ScreenVariant.SongList(defaultArtist)))
        localViewModel.submitAction(UpdateArtists)
        artistsFlow.value = artists
        assertEquals(ScreenVariant.SongList(defaultArtist), localViewModel.appStateFlow.value.currentScreenVariant)
        assertEquals(artists, localViewModel.appStateFlow.value.artistList)
    }

    @Test
    fun test002_selectScreen_Favorite_isWorkingCorrect() {
        val artists = listOf("Первый", "Второй", "Третий")
        localViewModel.submitAction(SelectScreen(ScreenVariant.Favorite()))
        localViewModel.submitAction(UpdateArtists)
        artistsFlow.value = artists
        assertEquals(ScreenVariant.Favorite(), localViewModel.appStateFlow.value.currentScreenVariant)
        assertEquals(artists, localViewModel.appStateFlow.value.artistList)
    }

    @Test
    fun test101_showSongs_Kino_withPassToSong_isWorkingCorrect() {
        theSongs = songList
        localViewModel.submitAction(ShowSongs("Кино", "Кукушка"))

        waitForCondition {
            localViewModel.appStateFlow.value.currentArtist == "Кино"
        }

        assertEquals("Кино", localViewModel.appStateFlow.value.currentArtist)

        waitForCondition {
            localViewModel.localStateFlow.value.currentSongList == songList
        }

        assertEquals(songList, localViewModel.localStateFlow.value.currentSongList)
        assertEquals(songList.size, localViewModel.commonSongTextStateFlow.value.currentSongCount)
    }

    @Test
    fun test102_selectArtist_Kino_and_Alisa_withoutPassToSong_isWorkingCorrect() {
        val songList2 = listOf(
            Song(artist = "Алиса", title = "title 1", text = "text text text"),
            Song(artist = "Алиса", title = "title 2", text = "text text text"),
            Song(artist = "Алиса", title = "title 3", text = "text text text"),
            Song(artist = "Алиса", title = "title 4", text = "text text text"),
            Song(artist = "Алиса", title = "title 5", text = "text text text")
        )

        theSongs = songList
        localViewModel.submitAction(SelectArtist("Кино"))

        waitForCondition {
            localViewModel.appStateFlow.value.currentArtist == "Кино"
        }
        assertEquals("Кино", localViewModel.appStateFlow.value.currentArtist)
        waitForCondition {
            localViewModel.localStateFlow.value.currentSongList == songList
        }
        assertEquals(songList, localViewModel.localStateFlow.value.currentSongList)
        localViewModel.submitAction(SelectArtist("Алиса"))
        theSongs = songList2
        waitForCondition {
            localViewModel.localStateFlow.value.currentSongList == songList2
        }
        assertEquals(songList2, localViewModel.localStateFlow.value.currentSongList)
    }

    @Test
    fun test103_selectArtist_addArtist_isWorkingCorrect() {
        localViewModel.submitAction(SelectArtist(ARTIST_ADD_ARTIST))

        assertEquals(ScreenVariant.AddArtist, localViewModel.appStateFlow.value.currentScreenVariant)
    }

    @Test
    fun test104_selectArtist_addSong_isWorkingCorrect() {
        localViewModel.submitAction(SelectArtist(ARTIST_ADD_SONG))

        assertEquals(ScreenVariant.AddSong, localViewModel.appStateFlow.value.currentScreenVariant)
    }

    @Test
    fun test105_selectArtist_cloudSongs_isWorkingCorrect() {
        localViewModel.submitAction(SelectArtist(ARTIST_CLOUD_SONGS))

        assertTrue(localViewModel.appStateFlow.value.currentScreenVariant is ScreenVariant.CloudSearch)
    }

    @Test
    fun test106_selectArt_Donaisttion_isWorkingCorrect() {
        localViewModel.submitAction(SelectArtist(ARTIST_DONATION))

        assertEquals(ScreenVariant.Donation, localViewModel.appStateFlow.value.currentScreenVariant)
    }

    @Test
    fun test107_selectSong_isWorkingCorrect() {
        theSongs = songList

        TimeUnit.MILLISECONDS.sleep(500)

        localViewModel.submitAction(SelectArtist("Кино"))

        TimeUnit.MILLISECONDS.sleep(500)

        localViewModel.submitAction(SelectSong(13))

        TimeUnit.MILLISECONDS.sleep(500)

        assertEquals(13, localViewModel.commonSongTextStateFlow.value.songListScrollPosition)
        assertEquals(true, localViewModel.commonSongTextStateFlow.value.songListNeedScroll)
        assertEquals(songList[0], localViewModel.commonSongTextStateFlow.value.currentSong)
        assertEquals(13, localViewModel.commonSongTextStateFlow.value.currentSongPosition)
    }

    @Test
    fun test108A_setFavorite_isWorkingCorrect() {
        localViewModel.submitAction(SelectSong(13))
        waitForCondition {
            localViewModel.commonSongTextStateFlow.value.currentSongPosition == 13
        }

        localViewModel.submitAction(SetFavorite(true))
        waitForCondition {
            localViewModel.commonSongTextStateFlow.value.currentSong?.favorite == true
        }
        assertEquals(true, localViewModel.commonSongTextStateFlow.value.currentSong?.favorite)
        val song1 = localViewModel.commonSongTextStateFlow.value.currentSong ?: throw IllegalStateException()

        localViewModel.submitAction(SetFavorite(false))
        waitForCondition {
            localViewModel.commonSongTextStateFlow.value.currentSong?.favorite == false
        }
        assertEquals(false, localViewModel.commonSongTextStateFlow.value.currentSong?.favorite)
        val song2 = localViewModel.commonSongTextStateFlow.value.currentSong ?: throw IllegalStateException()

        verifyAll {
            toasts.showToast(R.string.toast_added_to_favorite)
            toasts.showToast(R.string.toast_removed_from_favorite)
        }
    }

    @Test
    fun test108B_deleteFromFavorite_singleSong_isWorkingCorrect() {
        val song = songList[0].copy(favorite = true)
        theSongs = listOf(song)
        theSong = song

        TimeUnit.MILLISECONDS.sleep(500)
        localViewModel.submitAction(SelectScreen(ScreenVariant.Favorite()))

        TimeUnit.MILLISECONDS.sleep(500)
        localViewModel.submitAction(SelectArtist(ARTIST_FAVORITE))

        TimeUnit.MILLISECONDS.sleep(500)
        localViewModel.submitAction(SelectSong(0))

        TimeUnit.MILLISECONDS.sleep(500)
        localViewModel.submitAction(SelectScreen(ScreenVariant.SongText(ARTIST_FAVORITE, 0)))

        TimeUnit.MILLISECONDS.sleep(500)
        localViewModel.submitAction(SetFavorite(false))

        TimeUnit.MILLISECONDS.sleep(500)

        println(localViewModel.commonSongTextStateFlow.value.toString())

        assertEquals(false, localViewModel.commonSongTextStateFlow.value.currentSong?.favorite)
        assertEquals(ScreenVariant.Favorite(isBackFromSomeScreen = true), appStateHolder.appStateFlow.value.currentScreenVariant)

        verifyAll {
            toasts.showToast(R.string.toast_removed_from_favorite)
        }
    }

    @Test
    fun test108C_deleteFromFavorite_lastSongAtList_isWorkingCorrect() {
        val song = songList[0]
        theSongs = listOf(songList[1], songList[2], song).map {
            it.copy(favorite = true)
        }
        theSong = song.copy(favorite = true)

        TimeUnit.MILLISECONDS.sleep(500)
        localViewModel.submitAction(SelectScreen(ScreenVariant.Favorite()))

        TimeUnit.MILLISECONDS.sleep(500)
        localViewModel.submitAction(SelectArtist(ARTIST_FAVORITE))

        TimeUnit.MILLISECONDS.sleep(500)
        localViewModel.submitAction(SelectSong(2))

        TimeUnit.MILLISECONDS.sleep(500)
        localViewModel.submitAction(SelectScreen(ScreenVariant.SongText(ARTIST_FAVORITE, 2)))

        TimeUnit.MILLISECONDS.sleep(500)
        localViewModel.submitAction(SetFavorite(false))

        TimeUnit.MILLISECONDS.sleep(500)

        println(localViewModel.commonSongTextStateFlow.value.toString())

        assertEquals(false, localViewModel.commonSongTextStateFlow.value.currentSong?.favorite)
        assertEquals(ScreenVariant.SongText(ARTIST_FAVORITE, 1), appStateHolder.appStateFlow.value.currentScreenVariant)

        verifyAll {
            toasts.showToast(R.string.toast_removed_from_favorite)
        }
    }

    @Test
    fun test108D_deleteFromFavorite_notLastSongAtList_isWorkingCorrect() {
        val song = songList[0]
        theSongs = listOf(songList[1], song, songList[2]).map {
            it.copy(favorite = true)
        }
        theSong = song.copy(favorite = true)

        TimeUnit.MILLISECONDS.sleep(500)
        localViewModel.submitAction(SelectScreen(ScreenVariant.Favorite()))

        TimeUnit.MILLISECONDS.sleep(500)
        localViewModel.submitAction(SelectArtist(ARTIST_FAVORITE))

        TimeUnit.MILLISECONDS.sleep(500)
        localViewModel.submitAction(SelectSong(1))

        TimeUnit.MILLISECONDS.sleep(500)
        localViewModel.submitAction(SelectScreen(ScreenVariant.SongText(ARTIST_FAVORITE, 1)))

        TimeUnit.MILLISECONDS.sleep(500)
        localViewModel.submitAction(SetFavorite(false))

        TimeUnit.MILLISECONDS.sleep(500)

        println(localViewModel.commonSongTextStateFlow.value.toString())

        assertEquals(false, localViewModel.commonSongTextStateFlow.value.currentSong?.favorite)
        assertEquals(ScreenVariant.SongText(ARTIST_FAVORITE, 1), appStateHolder.appStateFlow.value.currentScreenVariant)

        verifyAll {
            toasts.showToast(R.string.toast_removed_from_favorite)
        }
    }

    @Test
    fun test109A_deleteCurrentToTrash_emptyList_isWorkingCorrect() {
        theSongs = listOf()

        localViewModel.submitAction(SelectArtist("Кино"))

        localViewModel.submitAction(SelectSong(13))
        waitForCondition {
            localViewModel.commonSongTextStateFlow.value.currentSongPosition == 13
        }
        val song = localViewModel.commonSongTextStateFlow.value.currentSong ?: throw IllegalStateException()

        localViewModel.submitAction(DeleteCurrentToTrash)

        verifyAll {
            deleteSongToTrashUseCase.execute(song)
            getCountByArtistUseCase.execute("Кино")
            toasts.showToast(R.string.toast_deleted_to_trash)
        }
    }

    @Test
    fun test109B_deleteCurrentToTrash_notEmptyList_regular_isWorkingCorrect() {
        theSongs = songList

        localViewModel.submitAction(SelectArtist("Кино"))

        TimeUnit.MILLISECONDS.sleep(500)

        localViewModel.submitAction(SelectSong(2))
        waitForCondition {
            localViewModel.commonSongTextStateFlow.value.currentSongPosition == 2
        }
        val song = localViewModel.commonSongTextStateFlow.value.currentSong ?: throw IllegalStateException()

        localViewModel.submitAction(DeleteCurrentToTrash)

        verifyAll {
            deleteSongToTrashUseCase.execute(song)
            getCountByArtistUseCase.execute("Кино")
            toasts.showToast(R.string.toast_deleted_to_trash)
        }
    }

    @Test
    fun test109C_deleteCurrentToTrash_notEmptyList_last_isWorkingCorrect() {
        theSongs = songList

        localViewModel.submitAction(SelectArtist("Кино"))

        TimeUnit.MILLISECONDS.sleep(500)

        localViewModel.submitAction(SelectSong(theSongs.size))
        waitForCondition {
            localViewModel.commonSongTextStateFlow.value.currentSongPosition == theSongs.size
        }
        val song = localViewModel.commonSongTextStateFlow.value.currentSong ?: throw IllegalStateException()

        localViewModel.submitAction(DeleteCurrentToTrash)

        verifyAll {
            deleteSongToTrashUseCase.execute(song)
            getCountByArtistUseCase.execute("Кино")
            toasts.showToast(R.string.toast_deleted_to_trash)
        }
    }

    @Test
    fun test110_openingYandexMusic_isWorkingCorrect() {
        localViewModel.submitAction(SelectSong(2))

        waitForCondition {
            localViewModel.commonSongTextStateFlow.value.currentSongPosition == 2
        }
        val song = localViewModel.commonSongTextStateFlow.value.currentSong ?: throw IllegalStateException()
        val searchFor = song.searchFor

        localViewModel.submitAction(OpenYandexMusic(true))

        verifyAll {
            callbacks.onOpenYandexMusic(searchFor)
        }
    }

    @Test
    fun test111_openingYoutubeMusic_isWorkingCorrect() {
        localViewModel.submitAction(SelectSong(2))

        waitForCondition {
            localViewModel.commonSongTextStateFlow.value.currentSongPosition == 2
        }
        val song = localViewModel.commonSongTextStateFlow.value.currentSong ?: throw IllegalStateException()
        val searchFor = song.searchFor

        localViewModel.submitAction(OpenYoutubeMusic(true))

        verifyAll {
            callbacks.onOpenYoutubeMusic(searchFor)
        }
    }

    @Test
    fun test112_openingVkMusic_isWorkingCorrect() {
        localViewModel.submitAction(SelectSong(2))

        waitForCondition {
            localViewModel.commonSongTextStateFlow.value.currentSongPosition == 2
        }
        val song = localViewModel.commonSongTextStateFlow.value.currentSong ?: throw IllegalStateException()
        val searchFor = song.searchFor

        localViewModel.submitAction(OpenVkMusic(true))

        verifyAll {
            callbacks.onOpenVkMusic(searchFor)
        }
    }

    @Test
    fun test113_sendingWarning_isWorkingCorrect() {
        localViewModel.submitAction(SelectSong(2))

        waitForCondition {
            localViewModel.commonSongTextStateFlow.value.currentSongPosition == 2
        }
        val song = localViewModel.commonSongTextStateFlow.value.currentSong ?: throw IllegalStateException()

        localViewModel.submitAction(SendWarning("some comment"))

        TimeUnit.MILLISECONDS.sleep(500L)

        coVerifyAll {
            addWarningUseCase.execute(song, "some comment")
        }
    }

    @Test
    fun test114_nextSong_regular_isWorkingCorrect() {
        theSongs = songList + songList + songList + songList

        localViewModel.submitAction(SelectArtist("Кино"))

        TimeUnit.MILLISECONDS.sleep(500)

        localViewModel.submitAction(SelectSong(13))

        TimeUnit.MILLISECONDS.sleep(500)

        localViewModel.submitAction(SelectScreen(ScreenVariant.SongText("Кино", 13)))

        TimeUnit.MILLISECONDS.sleep(500)

        localViewModel.submitAction(NextSong)

        TimeUnit.MILLISECONDS.sleep(500)

        assertEquals(ScreenVariant.SongText("Кино", 14), localViewModel.appStateFlow.value.currentScreenVariant)
    }

    @Test
    fun test115_nextSong_last_isWorkingCorrect() {
        theSongs = songList + songList + songList + songList

        localViewModel.submitAction(SelectArtist("Кино"))

        TimeUnit.MILLISECONDS.sleep(500)

        localViewModel.submitAction(SelectSong(theSongs.size - 1))

        TimeUnit.MILLISECONDS.sleep(500)

        localViewModel.submitAction(SelectScreen(ScreenVariant.SongText("Кино", 13)))

        TimeUnit.MILLISECONDS.sleep(500)

        localViewModel.submitAction(NextSong)

        TimeUnit.MILLISECONDS.sleep(500)

        assertEquals(ScreenVariant.SongText("Кино", 0), localViewModel.appStateFlow.value.currentScreenVariant)
    }

    @Test
    fun test116_prevSong_regular_isWorkingCorrect() {
        theSongs = songList + songList + songList + songList

        localViewModel.submitAction(SelectArtist("Кино"))

        TimeUnit.MILLISECONDS.sleep(500)

        localViewModel.submitAction(SelectSong(13))

        TimeUnit.MILLISECONDS.sleep(500)

        localViewModel.submitAction(SelectScreen(ScreenVariant.SongText("Кино", 13)))

        TimeUnit.MILLISECONDS.sleep(500)

        localViewModel.submitAction(PrevSong)

        TimeUnit.MILLISECONDS.sleep(500)

        assertEquals(ScreenVariant.SongText("Кино", 12), localViewModel.appStateFlow.value.currentScreenVariant)
    }

    @Test
    fun test117_prevSong_first_isWorkingCorrect() {
        theSongs = songList + songList + songList + songList

        localViewModel.submitAction(SelectArtist("Кино"))

        TimeUnit.MILLISECONDS.sleep(500)

        localViewModel.submitAction(SelectSong(0))

        TimeUnit.MILLISECONDS.sleep(500)

        localViewModel.submitAction(SelectScreen(ScreenVariant.SongText("Кино", 0)))

        TimeUnit.MILLISECONDS.sleep(500)

        localViewModel.submitAction(PrevSong)

        TimeUnit.MILLISECONDS.sleep(500)

        assertEquals(ScreenVariant.SongText("Кино", theSongs.size - 1), localViewModel.appStateFlow.value.currentScreenVariant)
    }

    @Test
    fun test118_updateCurrentSong_isWorkingCorrect() {
        val song = songList[0]

        localViewModel.submitAction(UpdateCurrentSong(song))

        TimeUnit.MILLISECONDS.sleep(500)

        assertEquals(song, localViewModel.commonSongTextStateFlow.value.currentSong)
    }

    @Test
    fun test119_updateMenuScrollPosition_isWorkingCorrect() {
        localViewModel.submitAction(UpdateMenuScrollPosition(5))

        TimeUnit.MILLISECONDS.sleep(500)

        assertEquals(5, localViewModel.localStateFlow.value.menuScrollPosition)
    }

    @Test
    fun test120_updateMenuExpandedArtistGroup_isWorkingCorrect() {
        localViewModel.submitAction(UpdateMenuExpandedArtistGroup("F"))

        TimeUnit.MILLISECONDS.sleep(500)

        assertEquals("F", localViewModel.localStateFlow.value.menuExpandedArtistGroup)
    }

    @Test
    fun test121_updateSongListScrollPosition_isWorkingCorrect() {
        localViewModel.submitAction(UpdateSongListScrollPosition(5))

        TimeUnit.MILLISECONDS.sleep(500)

        assertEquals(5, localViewModel.commonSongTextStateFlow.value.songListScrollPosition)
    }

    @Test
    fun test122_updateSongListNeedScroll_isWorkingCorrect() {
        localViewModel.submitAction(UpdateSongListNeedScroll(true))

        TimeUnit.MILLISECONDS.sleep(500)

        assertEquals(true, localViewModel.commonSongTextStateFlow.value.songListNeedScroll)
    }

    @Test
    fun test123_setEditorMode_isWorkingCorrect() {
        localViewModel.submitAction(SetEditorMode(true))

        TimeUnit.MILLISECONDS.sleep(500)

        assertEquals(true, localViewModel.commonSongTextStateFlow.value.isEditorMode)
    }

    @Test
    fun test124_setAutoPlayMode_isWorkingCorrect() {
        localViewModel.submitAction(SetAutoPlayMode(true))

        TimeUnit.MILLISECONDS.sleep(500)

        assertEquals(true, localViewModel.commonSongTextStateFlow.value.isAutoPlayMode)
    }

    @Test
    fun test125_reviewApp_isWorkingCorrect() {
        localViewModel.submitAction(ReviewApp)

        verifyAll {
            callbacks.onReviewApp()
        }
    }

    @Test
    fun test125_openDevSite_isWorkingCorrect() {
        localViewModel.submitAction(ShowDevSite)

        verifyAll {
            callbacks.onShowDevSite()
        }
    }
}
