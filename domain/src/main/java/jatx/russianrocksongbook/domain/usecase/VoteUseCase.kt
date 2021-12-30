package jatx.russianrocksongbook.domain.usecase

import jatx.russianrocksongbook.domain.models.CloudSong
import jatx.russianrocksongbook.domain.repository.CloudRepository
import jatx.russianrocksongbook.domain.models.interfaces.UserInfo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VoteUseCase @Inject constructor(
    private val cloudRepository: CloudRepository,
    private val userInfo: UserInfo
) {
    fun execute(cloudSong: CloudSong, voteValue: Int) = cloudRepository
        .vote(cloudSong, userInfo, voteValue)
}