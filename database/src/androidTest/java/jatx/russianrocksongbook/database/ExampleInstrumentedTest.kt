package jatx.russianrocksongbook.database

import android.util.Log
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import jatx.russianrocksongbook.database.db.AppDatabase
import jatx.russianrocksongbook.database.db.dao.SongDao
import jatx.russianrocksongbook.database.dbinit.LocalRepositoryInitializerImpl
import jatx.russianrocksongbook.database.dbinit.artistMap
import jatx.russianrocksongbook.database.repository.LocalRepositoryImpl
import jatx.russianrocksongbook.database.repository.predefinedList
import jatx.russianrocksongbook.domain.repository.LocalRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    private lateinit var db: AppDatabase
    private lateinit var songDao: SongDao
    private lateinit var localRepo: LocalRepository

    @Before
    fun createDb() {
        db = Room
            .inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                AppDatabase::class.java
            )
            .build()
        songDao = db.songDao()
        localRepo = LocalRepositoryImpl(songDao)
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun test1_fillDbFromJSON() {
        val localRepositoryInitializer = LocalRepositoryInitializerImpl(
            localRepo,
            ApplicationProvider.getApplicationContext()
        )
        runBlocking {
            localRepositoryInitializer.fillDbFromJSON().collect {
                Log.e("fillDbFromJSON", it.toString())
            }
        }
        val artists1 = artistMap.keys.toList()
        val artists2 = localRepo.getArtistsAsList()
        artists1.forEach {
            assert(it in artists2)
            Log.e("artist", it)
        }
        predefinedList.forEach {
            assert(it in artists2)
            Log.e("artist", it)
        }
        assert(artists1.size + predefinedList.size == artists2.size)
        Log.e("size", "match")
        Log.e("artists1", artists1.toString())
        Log.e("artists2", artists2.toString())
        runBlocking {
            localRepo.getSongsByArtist(artists1[0]).take(1).collect { list ->
                assert(list.all { it.artist == artists1[0] })
                Log.e("song list","artist match")
            }
        }
    }
}