package jatx.russianrocksongbook.data.impl

import io.reactivex.Flowable
import jatx.russianrocksongbook.data.SongRepository
import jatx.russianrocksongbook.db.dao.SongDao
import jatx.russianrocksongbook.domain.CloudSong
import jatx.russianrocksongbook.domain.Song
import jatx.russianrocksongbook.domain.USER_SONG_MD5
import jatx.russianrocksongbook.domain.songTextHash
import javax.inject.Inject

const val ARTIST_FAVORITE = "Избранное"
const val ARTIST_ADD_ARTIST = "Добавить исполнителя"
const val ARTIST_ADD_SONG = "Добавить песню"
const val ARTIST_CLOUD_SONGS = "Аккорды онлайн"
const val ARTIST_DONATION = "Пожертвования"

class SongRepositoryImpl @Inject constructor(
    private val songDao: SongDao
): SongRepository {
    override fun getArtists(): Flowable<List<String>> {
        return songDao
            .getArtistsFlowable()
            .map {
                val arrayList = arrayListOf<String>()
                arrayList.add(ARTIST_FAVORITE)
                arrayList.add(ARTIST_ADD_ARTIST)
                arrayList.add(ARTIST_ADD_SONG)
                arrayList.add(ARTIST_CLOUD_SONGS)
                arrayList.add(ARTIST_DONATION)

                arrayList.addAll(it)

                arrayList
            }
    }

    override fun getArtistsAsList(): List<String> {
        val arrayList = arrayListOf<String>()
        arrayList.add(ARTIST_FAVORITE)
        arrayList.add(ARTIST_ADD_ARTIST)
        arrayList.add(ARTIST_ADD_SONG)
        arrayList.add(ARTIST_CLOUD_SONGS)
        arrayList.add(ARTIST_DONATION)

        arrayList.addAll(songDao.getArtists())

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

