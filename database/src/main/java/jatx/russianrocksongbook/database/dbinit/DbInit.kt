package jatx.russianrocksongbook.database.dbinit

import android.content.Context
import jatx.russianrocksongbook.database.db.util.wrongArtists
import jatx.russianrocksongbook.database.db.util.wrongArtistsPatch
import jatx.russianrocksongbook.database.db.util.wrongSongs
import jatx.russianrocksongbook.domain.repository.LocalRepository

fun fillDbFromJSON(localRepo: LocalRepository, context: Context, onProgressChanged: (Int, Int) -> Unit) {
    val jsonLoader = JsonLoader(context)
    while (jsonLoader.hasNext()) {
        onProgressChanged(jsonLoader.current + 1, jsonLoader.total)
        val songs = jsonLoader.loadNext()
        localRepo.insertIgnoreSongs(songs)
    }
}

fun applySongPatches(localRepo: LocalRepository) {
    patches.forEach {
        localRepo.getSongByArtistAndTitle(it.artist, it.title)?.apply {
            val patchedText = this.text.replace(it.orig, it.patch)
            this.text = patchedText
            localRepo.updateSong(this)
        }
    }
}

fun deleteWrongSongs(localRepo: LocalRepository) {
    wrongSongs.forEach {
        localRepo.deleteWrongSong(it.artist, it.title)
    }
}

fun deleteWrongArtists(localRepo: LocalRepository) {
    wrongArtists.forEach {
        localRepo.deleteWrongArtist(it)
    }
}

fun patchWrongArtists(localRepo: LocalRepository) {
    wrongArtistsPatch.keys.forEach { key ->
        localRepo.patchWrongArtist(key, wrongArtistsPatch[key]!!)
    }
}