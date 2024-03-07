package jatx.russianrocksongbook.domain.usecase.local

import jatx.russianrocksongbook.domain.models.local.Song
import jatx.russianrocksongbook.domain.repository.local.LocalRepository
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class DeleteSongToTrashUseCase @Inject constructor(
    private val localRepository: LocalRepository
) {
    fun execute(song: Song) = localRepository.deleteSongToTrash(song)
}