package jatx.russianrocksongbook.domain.usecase

import jatx.russianrocksongbook.domain.models.Song
import jatx.russianrocksongbook.domain.repository.CloudRepository
import jatx.russianrocksongbook.domain.models.interfaces.UserInfo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddSongToCloudUseCase @Inject constructor(
    private val cloudRepository: CloudRepository,
    private val userInfo: UserInfo
) {
    fun execute(song: Song) = cloudRepository
        .addSong(song, userInfo)
}