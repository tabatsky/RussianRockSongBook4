package jatx.russianrocksongbook.domain.repository.init

interface LocalRepositoryInitializer {
    fun fillDbFromJSON(onProgressChanged: (Int, Int) -> Unit)
    fun applySongPatches()
    fun deleteWrongSongs()
    fun deleteWrongArtists()
    fun patchWrongArtists()
}