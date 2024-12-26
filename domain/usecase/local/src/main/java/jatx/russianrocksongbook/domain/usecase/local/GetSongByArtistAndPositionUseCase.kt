package jatx.russianrocksongbook.domain.usecase.local

import jatx.russianrocksongbook.domain.repository.local.LocalRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetSongByArtistAndPositionUseCase @Inject constructor(
    private val localRepository: LocalRepository
) {
    suspend fun execute(artist: String, position: Int) =
        localRepository.getSongByArtistAndPosition(artist, position)
}