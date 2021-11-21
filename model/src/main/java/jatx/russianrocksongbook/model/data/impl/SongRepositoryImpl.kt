package jatx.russianrocksongbook.model.data.impl

import dagger.hilt.components.SingletonComponent
import io.reactivex.Flowable
import it.czerwinski.android.hilt.annotations.BoundTo
import jatx.russianrocksongbook.model.data.*
import jatx.russianrocksongbook.model.db.dao.SongDao
import jatx.russianrocksongbook.model.domain.CloudSong
import jatx.russianrocksongbook.model.domain.Song
import jatx.russianrocksongbook.model.domain.USER_SONG_MD5
import jatx.russianrocksongbook.model.domain.songTextHash
import javax.inject.Inject

val predefinedList = listOf(
    ARTIST_FAVORITE,
    ARTIST_ADD_ARTIST,
    ARTIST_ADD_SONG,
    ARTIST_CLOUD_SONGS,
    ARTIST_DONATION
)

@BoundTo(supertype = SongRepository::class, component = SingletonComponent::class)
class SongRepositoryImpl @Inject constructor(
    private val songDao: SongDao
): SongRepository {
    override fun getArtists(): Flowable<List<String>> {
        return songDao
            .getArtistsFlowable()
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
        arrayList.addAll(songDao.getArtists().filter { !predefinedList.contains(it) })
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
                list.map { Song(it) }
            }

    override fun getSongByArtistAndPosition(artist: String, position: Int) =
        (if (artist == ARTIST_FAVORITE)
            songDao.getSongByPositionFavorite(position)
        else
            songDao.getSongByPositionAndArtist(position, artist))
            .map { Song(it) }


    override fun getSongByArtistAndTitle(artist: String, title: String): Song? {
        val songEntity = songDao.getSongByArtistAndTitle(artist, title)
        return if (songEntity != null) Song(songEntity) else null
    }

    override fun setFavorite(favorite: Boolean, artist: String, title: String) = songDao.setFavorite(favorite, artist, title)

    override fun updateSong(song: Song) {
        song.outOfTheBox = (songTextHash(song.text) == song.origTextMD5)
        songDao.updateSong(song.toSongEntity())
    }

    override fun deleteSongToTrash(song: Song) = songDao.setDeleted(true, song.artist, song.title)

    override fun addSongFromCloud(cloudSong: CloudSong) {
        val song = Song()
        song.artist = cloudSong.artist
        song.title = cloudSong.title
        song.text = cloudSong.text
        song.favorite = true
        song.outOfTheBox = true
        song.origTextMD5 = songTextHash(cloudSong.text)

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

    private fun updateSongText(song: Song) {
        songDao.updateSongText(song.artist, song.title, song.text, song.outOfTheBox)
    }
}

