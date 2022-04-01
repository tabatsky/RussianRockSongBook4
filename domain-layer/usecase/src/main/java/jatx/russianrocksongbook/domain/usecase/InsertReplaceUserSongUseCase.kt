package jatx.russianrocksongbook.domain.usecase

import jatx.russianrocksongbook.domain.models.local.Song
import jatx.russianrocksongbook.domain.repository.local.LocalRepository
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class InsertReplaceUserSongUseCase @Inject constructor(
    private val localRepository: LocalRepository
) {
    fun execute(song: Song) = localRepository.insertReplaceUserSong(song)
}