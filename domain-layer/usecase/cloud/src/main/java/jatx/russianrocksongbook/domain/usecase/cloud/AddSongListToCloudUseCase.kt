package jatx.russianrocksongbook.domain.usecase.cloud

import jatx.russianrocksongbook.domain.models.local.Song
import jatx.russianrocksongbook.domain.repository.cloud.CloudRepository
import jatx.russianrocksongbook.domain.models.cloud.UserInfo
import jatx.russianrocksongbook.domain.models.converters.withUserInfo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddSongListToCloudUseCase @Inject constructor(
    private val cloudRepository: CloudRepository,
    private val userInfo: UserInfo
) {
    fun execute(songs: List<Song>) = cloudRepository
        .addCloudSongList(songs.map {
            it withUserInfo userInfo
        })
}