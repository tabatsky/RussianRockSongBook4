package jatx.russianrocksongbook.domain.usecase.local

import jatx.russianrocksongbook.domain.models.cloud.CloudSong
import jatx.russianrocksongbook.domain.models.local.Song
import jatx.russianrocksongbook.domain.models.local.songTextHash
import jatx.russianrocksongbook.domain.repository.local.LocalRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddSongFromCloudUseCase @Inject constructor(
    private val localRepository: LocalRepository
) {
    fun execute(cloudSong: CloudSong) {
        val song = Song().apply {
            artist = cloudSong.artist
            title = cloudSong.visibleTitle
            text = cloudSong.text
            favorite = true
            outOfTheBox = true
            origTextMD5 = songTextHash(cloudSong.text)
        }

        localRepository.addSongFromCloud(song)
    }
}