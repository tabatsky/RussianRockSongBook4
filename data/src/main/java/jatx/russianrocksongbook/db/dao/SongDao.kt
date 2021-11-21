package jatx.russianrocksongbook.db.dao

import androidx.room.*
import io.reactivex.Flowable
import jatx.russianrocksongbook.db.entities.SongEntity

@Dao
interface SongDao {
    @Query("SELECT DISTINCT artist FROM songs WHERE deleted=0 ORDER BY artist")
    fun getArtistsFlowable(): Flowable<List<String>>

    @Query("SELECT DISTINCT artist FROM songs WHERE deleted=0 ORDER BY artist")
    fun getArtists(): List<String>

    @Query("SELECT COUNT(*) AS count FROM songs WHERE favorite=1 AND deleted=0")
    fun getCountFavorite(): Int

    @Query("SELECT COUNT(*) AS count FROM songs WHERE artist=:artist AND deleted=0")
    fun getCountByArtist(artist: String): Int

    @Query("SELECT * FROM songs WHERE favorite=1 AND deleted=0")
    fun getSongsFavorite(): Flowable<List<SongEntity>>

    @Query("SELECT * FROM songs WHERE artist=:artist AND deleted=0")
    fun getSongsByArtist(artist: String): Flowable<List<SongEntity>>

    @Query("""
        SELECT * FROM songs WHERE favorite=1 AND deleted=0 
        ORDER BY artist||title LIMIT 1 OFFSET :position
        """)
    fun getSongByPositionFavorite(position: Int): Flowable<SongEntity>

    @Query("""
        SELECT * FROM songs WHERE artist=:artist AND deleted=0 
        ORDER BY title LIMIT 1 OFFSET :position
    """)
    fun getSongByPositionAndArtist(position: Int, artist: String): Flowable<SongEntity>

    @Query("SELECT * FROM songs WHERE artist=:artist AND title=:title")
    fun getSongByArtistAndTitle(artist: String, title: String): SongEntity?

    @Query("SELECT favorite FROM songs WHERE artist=:artist AND title=:title")
    fun isSongFavorite(artist: String, title: String): Boolean

    @Query("SELECT * FROM songs WHERE artist=:artist")
    fun getAllSongsByArtist(artist: String): List<SongEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertIgnoreSongs(songs: List<SongEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReplaceSongs(songs: List<SongEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReplaceSong(song: SongEntity)

    @Query("UPDATE songs SET deleted=:deleted WHERE artist=:artist AND title=:title")
    fun setDeleted(deleted: Boolean, artist: String, title: String)

    @Query("UPDATE songs SET favorite=:favorite WHERE artist=:artist AND title=:title")
    fun setFavorite(favorite: Boolean, artist: String, title: String)

    @Query("DELETE FROM songs WHERE artist=:artist AND title=:title AND favorite=0 AND outOfTheBox=1")
    fun deleteWrongSong(artist: String, title: String)

    @Query("DELETE FROM songs WHERE artist=:artist AND favorite=0 AND outOfTheBox=1")
    fun deleteWrongArtist(artist: String)

    @Update
    fun updateSong(song: SongEntity)

    @Query("""
            UPDATE songs SET text=:text, deleted=0, outOfTheBox=:outOfTheBox 
            WHERE artist=:artist AND title=:title
    """)
    fun updateSongText(artist: String, title: String, text: String, outOfTheBox: Boolean)
}