package jatx.russianrocksongbook.database.dbinit

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import jatx.russianrocksongbook.database.db.util.wrongArtists
import jatx.russianrocksongbook.database.db.util.wrongArtistsPatch
import jatx.russianrocksongbook.database.db.util.wrongSongs
import jatx.russianrocksongbook.domain.repository.LocalRepository
import javax.inject.Inject

@ViewModelScoped
class DBInitializer @Inject constructor(
    private val localRepo: LocalRepository,
    @ApplicationContext private val context: Context
) {
    fun fillDbFromJSON(
        onProgressChanged: (Int, Int) -> Unit
    ) {
        val jsonLoader = JsonLoader(context)
        while (jsonLoader.hasNext()) {
            onProgressChanged(jsonLoader.current + 1, jsonLoader.total)
            val songs = jsonLoader.loadNext()
            localRepo.insertIgnoreSongs(songs)
        }
    }

    fun applySongPatches() {
        patches.forEach {
            localRepo.getSongByArtistAndTitle(it.artist, it.title)?.apply {
                val patchedText = this.text.replace(it.orig, it.patch)
                this.text = patchedText
                localRepo.updateSong(this)
            }
        }
    }

    fun deleteWrongSongs() {
        wrongSongs.forEach {
            localRepo.deleteWrongSong(it.artist, it.title)
        }
    }

    fun deleteWrongArtists() {
        wrongArtists.forEach {
            localRepo.deleteWrongArtist(it)
        }
    }

    fun patchWrongArtists() {
        wrongArtistsPatch.keys.forEach { key ->
            localRepo.patchWrongArtist(key, wrongArtistsPatch[key]!!)
        }
    }
}