package jatx.russianrocksongbook.domain.usecase.local

import jatx.russianrocksongbook.domain.repository.local.LocalRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetSongsByArtistUseCase @Inject constructor(
    private val localRepository: LocalRepository
) {
    suspend fun execute(artist: String) = localRepository.getSongsByArtist(artist)
}