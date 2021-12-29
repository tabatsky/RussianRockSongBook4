package jatx.russianrocksongbook.domain.usecase

import jatx.russianrocksongbook.domain.models.CloudSong
import jatx.russianrocksongbook.domain.repository.CloudSongRepository
import jatx.russianrocksongbook.domain.repository.OrderBy
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeleteFromCloudUseCase @Inject constructor(
    private val cloudSongRepository: CloudSongRepository
) {
    fun execute(secret1: String, secret2: String, cloudSong: CloudSong) = cloudSongRepository
        .delete(secret1, secret2, cloudSong)
}