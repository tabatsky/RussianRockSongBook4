package jatx.russianrocksongbook.domain.usecase.local

import jatx.russianrocksongbook.domain.models.cloud.CloudSong
import jatx.russianrocksongbook.domain.repository.local.LocalRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddSongFromCloudUseCase @Inject constructor(
    private val localRepository: LocalRepository
) {
    fun execute(cloudSong: CloudSong) = localRepository.addSongFromCloud(cloudSong)
}