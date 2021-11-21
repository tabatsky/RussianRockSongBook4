package jatx.russianrocksongbook.model.db

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import jatx.russianrocksongbook.model.db.dao.SongDao
import jatx.russianrocksongbook.model.db.entities.SongEntity

@Database(
    entities = [
        SongEntity::class
    ],
    version = 17,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao

    companion object {
        @Volatile private var instance: AppDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context): AppDatabase = instance ?: synchronized(LOCK){
            instance ?: buildDatabase(context).also {
                Log.e("db", "building")
                instance = it
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(context,
            AppDatabase::class.java, "songbook.db")
            .allowMainThreadQueries()
            .addMigrations(MIGRATION_16_17)
            .build()

        private val MIGRATION_16_17 = object: Migration(16, 17) {
            override fun migrate(database: SupportSQLiteDatabase) {
                Log.e("migrating", "16 to 17")
                //database.execSQL("ALTER TABLE songs ADD COLUMN id INTEGER DEFAULT 0")
                database.execSQL("ALTER TABLE songs RENAME TO old_songs")
                database.execSQL("""
                        CREATE TABLE songs 
                        (id INTEGER PRIMARY KEY AUTOINCREMENT,
                        artist TEXT NOT NULL,
                        title TEXT NOT NULL,
                        text TEXT NOT NULL,
                        favorite INTEGER NOT NULL DEFAULT 0,
                        deleted INTEGER NOT NULL DEFAULT 0 ,
                        outOfTheBox INTEGER NOT NULL DEFAULT 1,
                        origTextMD5 TEXT NOT NULL)
                        """)
                database.execSQL("""
                    CREATE UNIQUE INDEX the_index ON songs (artist, title) 
                """)
                database.execSQL("""
                    INSERT INTO songs 
                    (artist, title, text, favorite, deleted, outOfTheBox, origTextMD5) 
                    SELECT artist, title, text, favorite=='yes', deleted, outOfTheBox, origTextMD5 FROM old_songs
                """)
                database.execSQL("DROP TABLE old_songs")
            }
        }
    }
}