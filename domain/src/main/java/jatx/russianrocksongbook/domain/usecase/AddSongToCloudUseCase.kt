package jatx.russianrocksongbook.domain.usecase

import jatx.russianrocksongbook.domain.models.Song
import jatx.russianrocksongbook.domain.repository.CloudSongRepository
import jatx.russianrocksongbook.domain.models.interfaces.UserInfo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddSongToCloudUseCase @Inject constructor(
    private val cloudSongRepository: CloudSongRepository,
    private val userInfo: UserInfo
) {
    fun execute(song: Song) = cloudSongRepository
        .addSong(song, userInfo)
}