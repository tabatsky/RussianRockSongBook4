package jatx.russianrocksongbook.database.dbinit

import android.content.Context
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import it.czerwinski.android.hilt.annotations.BoundTo
import jatx.russianrocksongbook.domain.repository.LocalRepository
import jatx.russianrocksongbook.domain.repository.init.LocalRepositoryInitializer
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@ViewModelScoped
@BoundTo(supertype = LocalRepositoryInitializer::class, component = ViewModelComponent::class)
class LocalRepositoryInitializerImpl @Inject constructor(
    private val localRepo: LocalRepository,
    @ApplicationContext private val context: Context
): LocalRepositoryInitializer {
    override fun fillDbFromJSON() = flow {
        val jsonLoader = JsonLoader(context)
        while (jsonLoader.hasNext()) {
            val songs = jsonLoader.loadNext()
            localRepo.insertIgnoreSongs(songs)
            emit(jsonLoader.current to jsonLoader.total)
        }
    }

    override fun applySongPatches() {
        patches.forEach {
            localRepo.getSongByArtistAndTitle(it.artist, it.title)?.let { song ->
                val patchedText = song.text.replace(it.orig, it.patch)
                song.text = patchedText
                localRepo.updateSong(song)
            }
        }
    }

    override fun deleteWrongSongs() {
        wrongSongs.forEach {
            localRepo.deleteWrongSong(it.artist, it.title)
        }
    }

    override fun deleteWrongArtists() {
        wrongArtists.forEach {
            localRepo.deleteWrongArtist(it)
        }
    }

    override fun patchWrongArtists() {
        wrongArtistsPatch.keys.forEach { key ->
            localRepo.patchWrongArtist(key, wrongArtistsPatch[key]!!)
        }
    }
}