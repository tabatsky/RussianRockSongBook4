package jatx.russianrocksongbook.domain.usecase.cloud

import jatx.russianrocksongbook.domain.models.local.Song
import jatx.russianrocksongbook.domain.models.warning.Warning
import jatx.russianrocksongbook.domain.repository.cloud.CloudRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddWarningLocalUseCase @Inject constructor(
    private val cloudRepository: CloudRepository
) {
    fun execute(song: Song, comment: String) = cloudRepository
        .addWarning(Warning(song, comment))
}