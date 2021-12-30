package jatx.russianrocksongbook.domain.usecase

import jatx.russianrocksongbook.domain.models.CloudSong
import jatx.russianrocksongbook.domain.repository.CloudRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddWarningCloudUseCase @Inject constructor(
    private val cloudRepository: CloudRepository
) {
    fun execute(cloudSong: CloudSong, comment: String) = cloudRepository
        .addWarning(cloudSong, comment)
}