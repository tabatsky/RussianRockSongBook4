package jatx.russianrocksongbook.domain.usecase

import jatx.russianrocksongbook.domain.models.Song
import jatx.russianrocksongbook.domain.repository.CloudSongRepository
import jatx.russianrocksongbook.domain.models.interfaces.UserInfo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddSongListToCloudUseCase @Inject constructor(
    private val cloudSongRepository: CloudSongRepository,
    private val userInfo: UserInfo
) {
    fun execute(songs: List<Song>) = cloudSongRepository
        .addSongList(songs, userInfo)
}