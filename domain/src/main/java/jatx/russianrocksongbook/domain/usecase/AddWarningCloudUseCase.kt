package jatx.russianrocksongbook.domain.usecase

import jatx.russianrocksongbook.domain.models.CloudSong
import jatx.russianrocksongbook.domain.models.Song
import jatx.russianrocksongbook.domain.repository.CloudSongRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddWarningCloudUseCase @Inject constructor(
    private val cloudSongRepository: CloudSongRepository
) {
    fun execute(cloudSong: CloudSong, comment: String) = cloudSongRepository
        .addWarning(cloudSong, comment)
}