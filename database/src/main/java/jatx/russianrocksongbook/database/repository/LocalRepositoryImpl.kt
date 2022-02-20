package jatx.russianrocksongbook.database.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import dagger.hilt.components.SingletonComponent
import it.czerwinski.android.hilt.annotations.BoundTo
import jatx.russianrocksongbook.database.db.dao.SongDao
import jatx.russianrocksongbook.database.ext.toSong
import jatx.russianrocksongbook.database.ext.toSongEntity
import jatx.russianrocksongbook.domain.models.CloudSong
import jatx.russianrocksongbook.domain.models.Song
import jatx.russianrocksongbook.domain.models.USER_SONG_MD5
import jatx.russianrocksongbook.domain.repository.*
import jatx.russianrocksongbook.util.hashing.songTextHash
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

internal val predefinedList = listOf(
    ARTIST_FAVORITE,
    ARTIST_ADD_ARTIST,
    ARTIST_ADD_SONG,
    ARTIST_CLOUD_SONGS,
    ARTIST_DONATION
)

private val voiceSearchQuery: String
    get() {
        val symbolList = listOf(" ", "(", ")", "!", ".", ",", "-")
        var replacement1 = "LOWER(artist || title)"
        var replacement2 = "LOWER(title)"
        symbolList.forEach {
            replacement1 = "REPLACE($replacement1, '$it', '')"
            replacement2 = "REPLACE($replacement2, '$it', '')"
        }
        return "SELECT * FROM songs WHERE $replacement1 = ? OR $replacement2 = ?"
    }

private val voiceSearchQueryCached = voiceSearchQuery

@Singleton
@BoundTo(supertype = LocalRepository::class, component = SingletonComponent::class)
class LocalRepositoryImpl @Inject constructor(
    private val songDao: SongDao
): LocalRepository {
    override fun getArtists(): Flow<List<String>> {
        return songDao
            .getArtists()
            .map {
                val arrayList = arrayListOf<String>()
                arrayList.addAll(predefinedList)
                arrayList.addAll(it.filter { !predefinedList.contains(it) })

                arrayList
            }
    }

    override fun getArtistsAsList(): List<String> {
        val arrayList = arrayListOf<String>()
        arrayList.addAll(predefinedList)
        arrayList.addAll(songDao.getArtistsAsList().filter { !predefinedList.contains(it) })
        return arrayList
    }

    override fun getCountByArtist(artist: String) =
        if (artist == ARTIST_FAVORITE)
            songDao.getCountFavorite()
        else
            songDao.getCountByArtist(artist)

    override fun getSongsByArtist(artist: String) =
        (if (artist == ARTIST_FAVORITE)
            songDao.getSongsFavorite()
        else
            songDao.getSongsByArtist(artist))
            .map { list ->
                list.map { it.toSong() }
            }

    override fun getSongsByArtistAsList(artist: String) =
        (if (artist == ARTIST_FAVORITE)
            songDao.getSongsFavoriteAsList()
        else
            songDao.getSongsByArtistAsList(artist))
            .map { it.toSong() }

    override fun getSongsByVoiceSearch(voiceSearch: String): List<Song> {
        val query = SimpleSQLiteQuery(voiceSearchQueryCached, arrayOf(voiceSearch, voiceSearch))
        return songDao
            .getSongsRawQuery(query)
            .map { it.toSong() }
    }

    override fun getSongByArtistAndPosition(artist: String, position: Int) =
        (if (artist == ARTIST_FAVORITE)
            songDao.getSongByPositionFavorite(position)
        else
            songDao.getSongByPositionAndArtist(position, artist))
            .map { it?.toSong() }


    override fun getSongByArtistAndTitle(artist: String, title: String): Song? {
        return songDao.getSongByArtistAndTitle(artist, title)?.toSong()
    }

    override fun setFavorite(favorite: Boolean, artist: String, title: String) = songDao.setFavorite(favorite, artist, title)

    override fun updateSong(song: Song) {
        song.outOfTheBox = (songTextHash(song.text) == song.origTextMD5)
        songDao.updateSong(song.toSongEntity())
    }

    override fun deleteSongToTrash(song: Song) = songDao.setDeleted(true, song.artist, song.title)

    override fun addSongFromCloud(cloudSong: CloudSong) {
        val song = Song().apply {
            artist = cloudSong.artist
            title = cloudSong.title
            text = cloudSong.text
            favorite = true
            outOfTheBox = true
            origTextMD5 = songTextHash(cloudSong.text)
        }

        if (songDao.getSongByArtistAndTitle(cloudSong.artist, cloudSong.visibleTitle) == null) {
            songDao.insertReplaceSong(song.toSongEntity())
        } else {
            updateSongText(song)
            setFavorite(true, cloudSong.artist, cloudSong.visibleTitle)
        }
    }

    override fun isSongFavorite(artist: String, title: String) = songDao.isSongFavorite(artist, title)

    override fun insertReplaceUserSongs(songs: List<Song>): List<Song> {
        val actualSongs = songs.map {
            it.favorite = isSongFavorite(it.artist, it.title)
            it.origTextMD5 = USER_SONG_MD5
            it.outOfTheBox = false
            it
        }
        songDao.insertReplaceSongs(actualSongs.map { it.toSongEntity() })
        return actualSongs
    }

    override fun insertIgnoreSongs(songs: List<Song>) = songDao
        .insertIgnoreSongs(songs.map { it.toSongEntity() })

    override fun insertReplaceUserSong(song: Song): Song {
        song.favorite = isSongFavorite(song.artist, song.title)
        song.origTextMD5 = USER_SONG_MD5
        song.outOfTheBox = false
        songDao.insertReplaceSong(song.toSongEntity())
        return song
    }

    override fun deleteWrongSong(artist: String, title: String) = songDao.deleteWrongSong(artist, title)

    override fun deleteWrongArtist(artist: String) = songDao.deleteWrongArtist(artist)

    override fun patchWrongArtist(wrongArtist: String, actualArtist: String) =
        songDao.patchWrongArtist(wrongArtist, actualArtist)

    private fun updateSongText(song: Song) {
        songDao.updateSongText(song.artist, song.title, song.text, song.outOfTheBox)
    }
}

