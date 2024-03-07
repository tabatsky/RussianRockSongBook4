package jatx.russianrocksongbook.database.dbinit

import android.content.Context
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import it.czerwinski.android.hilt.annotations.BoundTo
import jatx.russianrocksongbook.domain.repository.local.LocalRepository
import jatx.russianrocksongbook.domain.repository.local.init.LocalRepositoryInitializer
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@ViewModelScoped
@BoundTo(supertype = LocalRepositoryInitializer::class, component = ViewModelComponent::class)
class LocalRepositoryInitializerImpl @Inject constructor(
    private val localRepo: LocalRepository,
    @ApplicationContext private val context: Context
): LocalRepositoryInitializer {
    override fun fillDbFromJSONResources() = flow {
        val jsonResourceLoader = JsonResourceLoader(context)
        while (jsonResourceLoader.hasNext()) {
            val songs = jsonResourceLoader.loadNext()
            localRepo.insertIgnoreSongs(songs)
            emit(jsonResourceLoader.current to jsonResourceLoader.total)
        }
    }

    override suspend fun applySongPatches() {
        patches.forEach {
            localRepo.getSongByArtistAndTitle(it.artist, it.title)?.let { song ->
                val patchedText = song.text.replace(it.orig, it.patch)
                localRepo.updateSong(song.copy(text = patchedText))
            }
        }
    }

    override suspend fun deleteWrongSongs() {
        wrongSongs.forEach {
            localRepo.deleteWrongSong(it.artist, it.title)
        }
    }

    override suspend fun deleteWrongArtists() {
        wrongArtists.forEach {
            localRepo.deleteWrongArtist(it)
        }
    }

    override suspend fun patchWrongArtists() {
        wrongArtistsPatch.keys.forEach { key ->
            localRepo.patchWrongArtist(key, wrongArtistsPatch[key]!!)
        }
    }
}