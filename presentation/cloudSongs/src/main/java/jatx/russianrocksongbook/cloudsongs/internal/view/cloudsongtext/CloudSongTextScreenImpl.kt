package jatx.russianrocksongbook.cloudsongs.internal.view.cloudsongtext

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.paging.compose.collectAsLazyPagingItems
import jatx.russianrocksongbook.cloudsongs.internal.viewmodel.CloudViewModel

@Composable
internal fun CloudSongTextScreenImpl(position: Int) {
    val cloudViewModel = CloudViewModel.getInstance()

    val cloudState by cloudViewModel.cloudStateFlow.collectAsState()
    val cloudSongsFlow = cloudState.cloudSongsFlow
    val cloudSongItems = cloudSongsFlow?.collectAsLazyPagingItems()

    val vkMusicDontAsk by cloudViewModel.settings.vkMusicDontAskState.collectAsState()
    val yandexMusicDontAsk by cloudViewModel.settings.yandexMusicDontAskState.collectAsState()
    val youtubeMusicDontAsk by cloudViewModel.settings.youtubeMusicDontAskState.collectAsState()

    CloudSongTextScreenImplContent(
        position = position,
        cloudSongItems = cloudSongItems,
        listenToMusicVariant = cloudViewModel.settings.listenToMusicVariant,
        vkMusicDontAsk = vkMusicDontAsk,
        yandexMusicDontAsk = yandexMusicDontAsk,
        youtubeMusicDontAsk = youtubeMusicDontAsk,
        submitAction = cloudViewModel::submitAction
    )
}
