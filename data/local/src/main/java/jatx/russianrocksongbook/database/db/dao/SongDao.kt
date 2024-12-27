package jatx.russianrocksongbook.database.db.dao

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import jatx.russianrocksongbook.database.db.entities.SongEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {
    @Query("SELECT DISTINCT artist FROM songs WHERE deleted=0 ORDER BY artist")
    fun getArtists(): Flow<List<String>>

    @Query("SELECT DISTINCT artist FROM songs WHERE deleted=0 ORDER BY artist")
    fun getArtistsAsList(): List<String>

    @Query("SELECT COUNT(*) AS count FROM songs WHERE favorite=1 AND deleted=0")
    fun getCountFavorite(): Int

    @Query("SELECT COUNT(*) AS count FROM songs WHERE artist=:artist AND deleted=0")
    fun getCountByArtist(artist: String): Int

    @Query("SELECT * FROM songs WHERE favorite=1 AND deleted=0 ORDER BY artist||title")
    suspend fun getSongsFavorite(): List<SongEntity>

    @Query("SELECT * FROM songs WHERE artist=:artist AND deleted=0 ORDER BY title")
    suspend fun getSongsByArtist(artist: String): List<SongEntity>

    @Query("SELECT * FROM songs WHERE favorite=1 AND deleted=0 ORDER BY artist||title")
    fun getSongsFavoriteAsList(): List<SongEntity>

    @Query("SELECT * FROM songs WHERE artist=:artist AND deleted=0 ORDER BY title")
    fun getSongsByArtistAsList(artist: String): List<SongEntity>

    @RawQuery
    fun getSongsRawQuery(query: SupportSQLiteQuery): List<SongEntity>

    @Query("""
        SELECT * FROM songs WHERE favorite=1 AND deleted=0 
        ORDER BY artist||title LIMIT 1 OFFSET :position
        """)
    suspend fun getSongByPositionFavorite(position: Int): SongEntity?

    @Query("""
        SELECT * FROM songs WHERE artist=:artist AND deleted=0 
        ORDER BY title LIMIT 1 OFFSET :position
    """)
    suspend fun getSongByPositionAndArtist(position: Int, artist: String): SongEntity?

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

    @Query("""
        UPDATE OR REPLACE songs SET artist=:actualArtist
        WHERE artist=:wrongArtist AND outOfTheBox=1
    """)
    fun patchWrongArtist(wrongArtist: String, actualArtist: String)

    @Update
    fun updateSong(song: SongEntity)

    @Query("""
            UPDATE songs SET text=:text, deleted=0, outOfTheBox=:outOfTheBox 
            WHERE artist=:artist AND title=:title
    """)
    fun updateSongText(artist: String, title: String, text: String, outOfTheBox: Boolean)
}