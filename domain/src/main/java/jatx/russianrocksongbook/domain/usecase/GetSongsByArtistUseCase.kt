package jatx.russianrocksongbook.domain.usecase

import jatx.russianrocksongbook.domain.repository.SongRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetSongsByArtistUseCase @Inject constructor(
    private val songRepository: SongRepository
) {
    fun execute(artist: String) = songRepository.getSongsByArtist(artist)
}