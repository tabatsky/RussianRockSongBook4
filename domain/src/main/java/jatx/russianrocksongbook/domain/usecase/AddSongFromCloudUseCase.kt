package jatx.russianrocksongbook.domain.usecase

import jatx.russianrocksongbook.domain.models.CloudSong
import jatx.russianrocksongbook.domain.models.Song
import jatx.russianrocksongbook.domain.repository.SongRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddSongFromCloudUseCase @Inject constructor(
    private val songRepository: SongRepository
) {
    fun execute(cloudSong: CloudSong) = songRepository.addSongFromCloud(cloudSong)
}