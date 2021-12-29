package jatx.russianrocksongbook.domain.usecase

import jatx.russianrocksongbook.domain.models.Song
import jatx.russianrocksongbook.domain.repository.CloudSongRepository
import jatx.russianrocksongbook.domain.repository.SongRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddWarningLocalUseCase @Inject constructor(
    private val cloudSongRepository: CloudSongRepository
) {
    fun execute(song: Song, comment: String) = cloudSongRepository
        .addWarning(song, comment)
}