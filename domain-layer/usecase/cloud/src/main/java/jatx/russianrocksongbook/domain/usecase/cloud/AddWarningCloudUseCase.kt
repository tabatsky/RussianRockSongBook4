package jatx.russianrocksongbook.domain.usecase.cloud

import jatx.russianrocksongbook.domain.models.cloud.CloudSong
import jatx.russianrocksongbook.domain.models.warning.Warning
import jatx.russianrocksongbook.domain.repository.cloud.CloudRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddWarningCloudUseCase @Inject constructor(
    private val cloudRepository: CloudRepository
) {
    fun execute(cloudSong: CloudSong, comment: String) = cloudRepository
        .addWarning(Warning(cloudSong, comment))
}