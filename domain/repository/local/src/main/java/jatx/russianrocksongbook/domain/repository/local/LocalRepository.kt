package jatx.russianrocksongbook.domain.repository.local

import jatx.russianrocksongbook.domain.models.local.Song
import kotlinx.coroutines.flow.Flow

const val ARTIST_FAVORITE = "Избранное"
const val ARTIST_ADD_ARTIST = "Добавить исполнителя"
const val ARTIST_ADD_SONG = "Добавить песню"
const val ARTIST_CLOUD_SONGS = "Аккорды онлайн"
const val ARTIST_TEXT_SEARCH = "Поиск по тексту"
const val ARTIST_DONATION = "Пожертвования"

val predefinedArtistList = listOf(
    ARTIST_FAVORITE,
    ARTIST_ADD_ARTIST,
    ARTIST_ADD_SONG,
    ARTIST_CLOUD_SONGS,
    ARTIST_TEXT_SEARCH,
    ARTIST_DONATION
)

fun String.artistGroup() = this.first().toString().uppercase()

fun List<String>.artistGroups() = this.filter { it !in predefinedArtistList }
    .map { it.artistGroup() }
    .distinct()
    .sorted()

fun List<String>.predefinedArtistsWithGroups() =
    predefinedArtistList + this.artistGroups()

interface LocalRepository {
    fun getArtists(): Flow<List<String>>
    fun getArtistsAsList(): List<String>
    fun getCountByArtist(artist: String): Int
    suspend fun getSongsByArtist(artist: String): List<Song>
    fun getSongsByVoiceSearch(voiceSearch: String): List<Song>
    fun getSongsByTextSearch(words: List<String>, orderBy: TextSearchOrderBy): List<Song>
    suspend fun getSongByArtistAndPosition(artist: String, position: Int): Song?
    fun getSongByArtistAndTitle(artist: String, title: String): Song?
    fun setFavorite(favorite: Boolean, artist: String, title: String)
    fun updateSong(song: Song)
    fun deleteSongToTrash(song: Song)
    fun addSongFromCloud(song: Song)
    fun isSongFavorite(artist: String, title: String): Boolean
    fun insertIgnoreSongs(songs: List<Song>)
    fun insertReplaceUserSongs(songs: List<Song>): List<Song>
    fun insertReplaceUserSong(song: Song): Song
    fun deleteWrongSong(artist: String, title: String)
    fun deleteWrongArtist(artist: String)
    fun patchWrongArtist(wrongArtist: String, actualArtist: String)
}

enum class TextSearchOrderBy(
    val orderBy: String,
    val orderByRus: String
) {
    BY_TITLE("byTitle", "По названию"),
    BY_ARTIST("byArtist", "По исполнителю")
}

