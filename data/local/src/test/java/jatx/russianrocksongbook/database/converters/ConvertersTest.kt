package jatx.russianrocksongbook.database.converters

import jatx.russianrocksongbook.database.db.entities.SongEntity
import jatx.russianrocksongbook.database.dbinit.jsonresourcemodel.SongJsonResourceModel
import jatx.russianrocksongbook.domain.models.local.Song
import jatx.russianrocksongbook.domain.models.local.songTextHash
import org.junit.Assert.assertEquals
import org.junit.Test

const val ID = 137L
const val ARTIST = "some artist"
const val TITLE = "some title"
const val TEXT = "some text"
const val FAVORITE = true
const val DELETED = false
const val OUT_OF_THE_BOX = true
val ORIG_TEXT_MD5 = songTextHash(TEXT)

class ConvertersTest {
    @Test
    fun song_toSongEntity_isWorkingCorrect() {
        val song = Song(
            id = ID,
            artist = ARTIST,
            title = TITLE,
            text = TEXT,
            favorite = FAVORITE,
            deleted = DELETED,
            outOfTheBox = OUT_OF_THE_BOX,
            origTextMD5 = ORIG_TEXT_MD5
        )

        val songEntity = song.toSongEntity()

        assertEquals(songEntity.id, ID)
        assertEquals(songEntity.artist, ARTIST)
        assertEquals(songEntity.title, TITLE)
        assertEquals(songEntity.text, TEXT)
        assertEquals(songEntity.favorite, FAVORITE)
        assertEquals(songEntity.deleted, DELETED)
        assertEquals(songEntity.outOfTheBox, OUT_OF_THE_BOX)
        assertEquals(songEntity.origTextMD5, ORIG_TEXT_MD5)
    }

    @Test
    fun songEntity_toSong_isWorkingCorrect() {
        val songEntity = SongEntity(
            id = ID,
            artist = ARTIST,
            title = TITLE,
            text = TEXT,
            favorite = FAVORITE,
            deleted = DELETED,
            outOfTheBox = OUT_OF_THE_BOX,
            origTextMD5 = ORIG_TEXT_MD5
        )

        val song = songEntity.toSong()

        assertEquals(song.id, ID)
        assertEquals(song.artist, ARTIST)
        assertEquals(song.title, TITLE)
        assertEquals(song.text, TEXT)
        assertEquals(song.favorite, FAVORITE)
        assertEquals(song.deleted, DELETED)
        assertEquals(song.outOfTheBox, OUT_OF_THE_BOX)
        assertEquals(song.origTextMD5, ORIG_TEXT_MD5)
    }

    @Test
    fun songJsonResourceModel_asSongWithArtist_isWorkingCorrect() {
        val songJsonResourceModel = SongJsonResourceModel(
            title = TITLE,
            text = TEXT
        )

        val song = songJsonResourceModel asSongWithArtist ARTIST

        assertEquals(song.artist, ARTIST)
        assertEquals(song.title, TITLE)
        assertEquals(song.text, TEXT)
        assertEquals(song.origTextMD5, ORIG_TEXT_MD5)
    }
}