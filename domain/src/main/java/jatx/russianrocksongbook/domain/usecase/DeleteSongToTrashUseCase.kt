package jatx.russianrocksongbook.domain.usecase

import jatx.russianrocksongbook.domain.models.Song
import jatx.russianrocksongbook.domain.repository.SongRepository
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class DeleteSongToTrashUseCase @Inject constructor(
    private val songRepository: SongRepository
) {
    fun execute(song: Song) = songRepository.deleteSongToTrash(song)
}