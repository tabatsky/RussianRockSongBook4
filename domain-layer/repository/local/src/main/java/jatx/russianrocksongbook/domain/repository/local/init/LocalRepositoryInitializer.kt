package jatx.russianrocksongbook.domain.repository.local.init

import kotlinx.coroutines.flow.Flow

interface LocalRepositoryInitializer {
    fun fillDbFromJSON(): Flow<Pair<Int, Int>>
    fun applySongPatches()
    fun deleteWrongSongs()
    fun deleteWrongArtists()
    fun patchWrongArtists()
}