package jatx.russianrocksongbook.domain.usecase

import jatx.russianrocksongbook.domain.models.cloud.CloudSong
import jatx.russianrocksongbook.domain.repository.cloud.CloudRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeleteFromCloudUseCase @Inject constructor(
    private val cloudRepository: CloudRepository
) {
    fun execute(secret1: String, secret2: String, cloudSong: CloudSong) = cloudRepository
        .delete(secret1, secret2, cloudSong)
}