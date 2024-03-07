package jatx.russianrocksongbook.domain.usecase.local

import io.mockk.*
import io.mockk.junit5.MockKExtension
import jatx.russianrocksongbook.domain.models.cloud.CloudSong
import jatx.russianrocksongbook.domain.models.local.Song
import jatx.russianrocksongbook.domain.models.local.songTextHash
import jatx.russianrocksongbook.domain.repository.local.LocalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters

@MockKExtension.ConfirmVerification
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class LocalUseCaseTest {
    lateinit var localRepository: LocalRepository
    lateinit var addSongFromCloudUseCase: AddSongFromCloudUseCase
    lateinit var deleteSongToTrashUseCase: DeleteSongToTrashUseCase
    lateinit var getArtistsAsListUseCase: GetArtistsAsListUseCase
    lateinit var getArtistsUseCase: GetArtistsUseCase
    lateinit var getCountByArtistUseCase: GetCountByArtistUseCase
    lateinit var getSongByArtistAndPositionUseCase: GetSongByArtistAndPositionUseCase
    lateinit var getSongsByArtistUseCase: GetSongsByArtistUseCase
    lateinit var getSongsByVoiceSearchUseCase: GetSongsByVoiceSearchUseCase
    lateinit var insertReplaceUserSongsUseCase: InsertReplaceUserSongsUseCase
    lateinit var insertReplaceUserSongUseCase: InsertReplaceUserSongUseCase
    lateinit var updateSongUseCase: UpdateSongUseCase

    private val cloudSong = CloudSong(artist = "artist", title = "title", text = "text")
    private val song = Song(artist = "artist", title = "title", text = "text")
    private val songFlow = MutableStateFlow<Song?>(song)
    private val artistList = listOf("Александр Башлачёв", "Немного Нервно", "Сплин")
    private val artistsFlow = MutableStateFlow(artistList)
    private val songList = listOf(
        song.copy(title = "title 1"),
        song.copy(title = "title 2"),
        song.copy(title = "title 3"),
        song.copy(title = "title 4"),
        song.copy(title = "title 5")
    )
    private val songsFlow = MutableStateFlow(songList)

    @Before
    fun init() {
        localRepository = mockk(relaxed = true)
        addSongFromCloudUseCase = AddSongFromCloudUseCase(localRepository)
        deleteSongToTrashUseCase = DeleteSongToTrashUseCase(localRepository)
        getArtistsAsListUseCase = GetArtistsAsListUseCase(localRepository)
        getArtistsUseCase = GetArtistsUseCase(localRepository)
        getCountByArtistUseCase = GetCountByArtistUseCase(localRepository)
        getSongByArtistAndPositionUseCase = GetSongByArtistAndPositionUseCase(localRepository)
        getSongsByArtistUseCase = GetSongsByArtistUseCase(localRepository)
        getSongsByVoiceSearchUseCase = GetSongsByVoiceSearchUseCase(localRepository)
        insertReplaceUserSongsUseCase = InsertReplaceUserSongsUseCase(localRepository)
        insertReplaceUserSongUseCase = InsertReplaceUserSongUseCase(localRepository)
        updateSongUseCase = UpdateSongUseCase(localRepository)
    }

    @Test
    fun test001_addSongFromCloudUseCase_isWorkingCorrect() {
        every { localRepository.addSongFromCloud(any()) } just runs

        addSongFromCloudUseCase.execute(cloudSong)

        val song = Song(
            artist = cloudSong.artist,
            title = cloudSong.visibleTitle,
            text = cloudSong.text,
            favorite = true,
            outOfTheBox = true,
            origTextMD5 = songTextHash(cloudSong.text)
        )

        verifySequence {
            localRepository.addSongFromCloud(song)
        }
    }

    @Test
    fun test002_deleteSongToTrashUseCase_isWorkingCorrect() {
        every { localRepository.deleteSongToTrash(any()) } just runs

        deleteSongToTrashUseCase.execute(song)

        verifySequence {
            localRepository.deleteSongToTrash(song)
        }
    }

    @Test
    fun test003_getArtistsAsListUseCase_isWorkingCorrect() {
        every { localRepository.getArtistsAsList() } returns artistList

        val result = getArtistsAsListUseCase.execute()

        assertEquals(artistList, result)

        verifySequence {
            localRepository.getArtistsAsList()
        }
    }

    @Test
    fun test004_getArtistsUseCase_isWorkingCorrect() {
        every { localRepository.getArtists() } returns artistsFlow

        val result = getArtistsUseCase.execute()

        runBlocking {
            assertEquals(artistList, result.first())
        }

        verifySequence {
            localRepository.getArtists()
        }
    }

    @Test
    fun test005_getCountByArtistUseCase_isWorkingCorrect() {
        every { localRepository.getCountByArtist(any()) } returns 137

        val result = getCountByArtistUseCase.execute("Кино")

        assertEquals(137, result)

        verifySequence {
            localRepository.getCountByArtist("Кино")
        }
    }

    @Test
    fun test006_getSongByArtistAndPositionUseCase_isWorkingCorrect() {
        every { localRepository.getSongByArtistAndPosition(any(), any()) } returns songFlow

        val result = getSongByArtistAndPositionUseCase.execute("Кино", 13)

        runBlocking {
            assertEquals(song, result.first())
        }

        verifySequence {
            localRepository.getSongByArtistAndPosition("Кино", 13)
        }
    }

    @Test
    fun test007_getSongsByArtistUseCase_isWorkingCorrect() {
        every { localRepository.getSongsByArtist(any()) } returns songsFlow

        val result = getSongsByArtistUseCase.execute("Кино")

        runBlocking {
            assertEquals(songList, result.first())
        }

        verifySequence {
            localRepository.getSongsByArtist("Кино")
        }
    }

    @Test
    fun test008_getSongsByVoiceSearchUseCase_isWorkingCorrect() {
        every { localRepository.getSongsByVoiceSearch(any()) } returns songList

        val result = getSongsByVoiceSearchUseCase.execute("лалала")

        assertEquals(songList, result)

        verifySequence {
            localRepository.getSongsByVoiceSearch("лалала")
        }
    }

    @Test
    fun test009_insertReplaceUserSongsUseCase_isWorkingCorrect() {
        every { localRepository.insertReplaceUserSongs(any()) } returns songList

        val result = insertReplaceUserSongsUseCase.execute(songList)

        assertEquals(songList, result)

        verifySequence {
            localRepository.insertReplaceUserSongs(songList)
        }
    }

    @Test
    fun test010_insertReplaceUserSongUseCase_isWorkingCorrect() {
        every { localRepository.insertReplaceUserSong(any()) } returns song

        val result = insertReplaceUserSongUseCase.execute(song)

        assertEquals(song, result)

        verifySequence {
            localRepository.insertReplaceUserSong(song)
        }
    }

    @Test
    fun test011_updateSongUseCase_isWorkingCorrect() {
        every { localRepository.updateSong(any()) } just runs

        updateSongUseCase.execute(song)

        verifySequence {
            localRepository.updateSong(song)
        }
    }
}