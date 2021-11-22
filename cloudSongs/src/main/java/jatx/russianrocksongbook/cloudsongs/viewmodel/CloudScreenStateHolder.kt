package jatx.russianrocksongbook.cloudsongs.viewmodel

import jatx.russianrocksongbook.model.domain.CloudSong
import jatx.russianrocksongbook.viewmodel.ScreenStateHolder
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CloudScreenStateHolder @Inject constructor(
    val screenStateHolder: ScreenStateHolder
) {
    val isCloudLoading = MutableStateFlow(false)
    val cloudSongCount = MutableStateFlow(0)
    val cloudSongList = MutableStateFlow(listOf<CloudSong>())
    val cloudSongPosition = MutableStateFlow(0)
    val cloudSong: MutableStateFlow<CloudSong?> = MutableStateFlow(null)
}