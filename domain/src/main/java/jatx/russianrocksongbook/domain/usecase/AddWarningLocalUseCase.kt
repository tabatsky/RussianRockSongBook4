package jatx.russianrocksongbook.domain.usecase

import jatx.russianrocksongbook.domain.models.Song
import jatx.russianrocksongbook.domain.repository.CloudRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddWarningLocalUseCase @Inject constructor(
    private val cloudRepository: CloudRepository
) {
    fun execute(song: Song, comment: String) = cloudRepository
        .addWarning(song, comment)
}