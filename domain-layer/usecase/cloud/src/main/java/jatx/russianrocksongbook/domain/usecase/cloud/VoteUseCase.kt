package jatx.russianrocksongbook.domain.usecase.cloud

import jatx.russianrocksongbook.domain.models.cloud.CloudSong
import jatx.russianrocksongbook.domain.repository.cloud.CloudRepository
import jatx.russianrocksongbook.domain.models.cloud.UserInfo
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