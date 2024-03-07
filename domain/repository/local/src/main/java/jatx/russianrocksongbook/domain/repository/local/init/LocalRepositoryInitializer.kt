package jatx.russianrocksongbook.domain.repository.local.init

import kotlinx.coroutines.flow.Flow

interface LocalRepositoryInitializer {
    fun fillDbFromJSONResources(): Flow<Pair<Int, Int>>
    suspend fun applySongPatches()
    suspend fun deleteWrongSongs()
    suspend fun deleteWrongArtists()
    suspend fun patchWrongArtists()
}