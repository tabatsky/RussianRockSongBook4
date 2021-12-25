package jatx.russianrocksongbook.database.api

import android.content.Context
import jatx.russianrocksongbook.database.internal.db.util.JsonLoader
import jatx.russianrocksongbook.database.internal.db.util.patches
import jatx.russianrocksongbook.database.internal.db.util.wrongArtists
import jatx.russianrocksongbook.database.internal.db.util.wrongArtistsPatch
import jatx.russianrocksongbook.database.internal.db.util.wrongSongs

fun fillDbFromJSON(songRepo: SongRepository, context: Context, onProgressChanged: (Int, Int) -> Unit) {
    val jsonLoader = JsonLoader(context)
    while (jsonLoader.hasNext()) {
        onProgressChanged(jsonLoader.current + 1, jsonLoader.total)
        val songs = jsonLoader.loadNext()
        songRepo.insertIgnoreSongs(songs)
    }
}

fun applySongPatches(songRepo: SongRepository) {
    patches.forEach {
        songRepo.getSongByArtistAndTitle(it.artist, it.title)?.apply {
            val patchedText = this.text.replace(it.orig, it.patch)
            this.text = patchedText
            songRepo.updateSong(this)
        }
    }
}

fun deleteWrongSongs(songRepo: SongRepository) {
    wrongSongs.forEach {
        songRepo.deleteWrongSong(it.artist, it.title)
    }
}

fun deleteWrongArtists(songRepo: SongRepository) {
    wrongArtists.forEach {
        songRepo.deleteWrongArtist(it)
    }
}

fun patchWrongArtists(songRepo: SongRepository) {
    wrongArtistsPatch.keys.forEach { key ->
        songRepo.patchWrongArtist(key, wrongArtistsPatch[key]!!)
    }
}