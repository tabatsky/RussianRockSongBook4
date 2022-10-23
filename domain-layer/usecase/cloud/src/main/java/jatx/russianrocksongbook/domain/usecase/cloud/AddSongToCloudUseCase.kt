package jatx.russianrocksongbook.domain.usecase.cloud

import jatx.russianrocksongbook.domain.models.local.Song
import jatx.russianrocksongbook.domain.models.cloud.UserInfo
import jatx.russianrocksongbook.domain.models.converters.withUserInfo
import jatx.russianrocksongbook.domain.repository.cloud.CloudRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddSongToCloudUseCase @Inject constructor(
    private val cloudRepository: CloudRepository,
    private val userInfo: UserInfo
) {
    fun execute(song: Song) = cloudRepository
        .addCloudSong(song withUserInfo userInfo)
}