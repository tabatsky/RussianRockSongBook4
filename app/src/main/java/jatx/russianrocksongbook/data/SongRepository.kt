package jatx.russianrocksongbook.data

import io.reactivex.Flowable
import jatx.russianrocksongbook.db.dao.SongDao
import jatx.russianrocksongbook.db.entities.Song
import jatx.russianrocksongbook.db.entities.USER_SONG_MD5
import jatx.russianrocksongbook.db.entities.songTextHash
import jatx.russianrocksongbook.gson.CloudSong

const val ARTIST_FAVORITE = "Избранное"
const val ARTIST_ADD_ARTIST = "Добавить исполнителя"
const val ARTIST_ADD_SONG = "Добавить песню"
const val ARTIST_CLOUD_SONGS = "Аккорды онлайн"
const val ARTIST_DONATION = "Пожертвования"

class SongRepository(
    private val songDao: SongDao
) {
    fun getArtistsFlowable(): Flowable<List<String>> {
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

    fun getArtists(): List<String> {
        val arrayList = arrayListOf<String>()
        arrayList.add(ARTIST_FAVORITE)
        arrayList.add(ARTIST_ADD_ARTIST)
        arrayList.add(ARTIST_ADD_SONG)
        arrayList.add(ARTIST_CLOUD_SONGS)
        arrayList.add(ARTIST_DONATION)

        arrayList.addAll(songDao.getArtists())

        return arrayList
    }

    fun getCountByArtist(artist: String) =
        if (artist == ARTIST_FAVORITE)
            songDao.getCountFavorite()
        else
            songDao.getCountByArtist(artist)

    fun getSongsByArtist(artist: String) =
        if (artist == ARTIST_FAVORITE)
            songDao.getSongsFavorite()
        else
            songDao.getSongsByArtist(artist)

    fun getSongByArtistAndPosition(artist: String, position: Int) =
        if (artist == ARTIST_FAVORITE)
            songDao.getSongByPositionFavorite(position)
        else
            songDao.getSongByPositionAndArtist(position, artist)

    fun getSongByArtistAndTitle(artist: String, title: String) =
        songDao.getSongByArtistAndTitle(artist, title)

    fun setFavorite(favorite: Boolean, artist: String, title: String) = songDao.setFavorite(favorite, artist, title)

    fun updateSong(song: Song) {
        songDao.updateSong(
            song.withOutOfTheBox(
                songTextHash(song.text) == song.origTextMD5
            )
        )
    }

    fun deleteSongToTrash(song: Song) = songDao.setDeleted(true, song.artist, song.title)

    fun addSongFromCloud(cloudSong: CloudSong) {
        val song = Song()
            .withArtist(cloudSong.artist)
            .withTitle(cloudSong.visibleTitle)
            .withText(cloudSong.text)
            .withFavorite(true)
            .withOutOfTheBox(true)
            .withOrigTextMD5(songTextHash(cloudSong.text))

        if (songDao.getSongByArtistAndTitle(cloudSong.artist, cloudSong.visibleTitle) == null) {
            songDao.insertReplaceSong(song)
        } else {
            updateSongText(song)
            setFavorite(true, cloudSong.artist, cloudSong.visibleTitle)
        }
    }

    fun isSongFavorite(artist: String, title: String) = songDao.isSongFavorite(artist, title)

    fun insertReplaceUserSongs(songs: List<Song>): List<Song> {
        val actualSongs = songs.map {
            it
                .withFavorite(isSongFavorite(it.artist, it.title))
                .withOrigTextMD5(USER_SONG_MD5)
                .withOutOfTheBox(false)
        }
        songDao.insertReplaceSongs(actualSongs)
        return actualSongs
    }

    fun insertIgnoreSongs(songs: List<Song>) = songDao.insertIgnoreSongs(songs)

    fun insertReplaceUserSong(song: Song): Song {
        val actualSong = song
            .withFavorite(isSongFavorite(song.artist, song.title))
            .withOrigTextMD5(USER_SONG_MD5)
            .withOutOfTheBox(false)
        songDao.insertReplaceSong(actualSong)
        return actualSong
    }

    fun deleteWrongSong(artist: String, title: String) = songDao.deleteWrongSong(artist, title)

    fun deleteWrongArtist(artist: String) = songDao.deleteWrongArtist(artist)

    private fun updateSongText(song: Song) {
        songDao.updateSongText(song.artist, song.title, song.text, song.outOfTheBox)
    }
}

