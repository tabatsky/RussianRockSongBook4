package jatx.russianrocksongbook.domain.usecase

import jatx.russianrocksongbook.domain.models.Song
import jatx.russianrocksongbook.domain.repository.LocalRepository
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class InsertReplaceUserSongsUseCase @Inject constructor(
    private val localRepository: LocalRepository
) {
    fun execute(songs: List<Song>) = localRepository.insertReplaceUserSongs(songs)
}