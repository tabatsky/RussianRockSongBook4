package jatx.russianrocksongbook.domain.usecase

import jatx.russianrocksongbook.domain.models.CloudSong
import jatx.russianrocksongbook.domain.repository.LocalRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddSongFromCloudUseCase @Inject constructor(
    private val localRepository: LocalRepository
) {
    fun execute(cloudSong: CloudSong) = localRepository.addSongFromCloud(cloudSong)
}