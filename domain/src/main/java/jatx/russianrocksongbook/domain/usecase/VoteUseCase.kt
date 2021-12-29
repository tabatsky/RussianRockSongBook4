package jatx.russianrocksongbook.domain.usecase

import jatx.russianrocksongbook.domain.models.CloudSong
import jatx.russianrocksongbook.domain.repository.CloudSongRepository
import jatx.russianrocksongbook.domain.models.interfaces.UserInfo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VoteUseCase @Inject constructor(
    private val cloudSongRepository: CloudSongRepository,
    private val userInfo: UserInfo
) {
    fun execute(cloudSong: CloudSong, voteValue: Int) = cloudSongRepository
        .vote(cloudSong, userInfo, voteValue)
}