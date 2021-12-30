package jatx.russianrocksongbook.domain.usecase

import jatx.russianrocksongbook.domain.models.Song
import jatx.russianrocksongbook.domain.repository.LocalRepository
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class InsertReplaceUserSongUseCase @Inject constructor(
    private val localRepository: LocalRepository
) {
    fun execute(song: Song) = localRepository.insertReplaceUserSong(song)
}