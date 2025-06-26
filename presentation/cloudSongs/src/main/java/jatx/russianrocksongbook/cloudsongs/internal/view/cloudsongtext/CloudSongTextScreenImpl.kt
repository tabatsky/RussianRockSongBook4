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
    val currentCloudSongPosition = cloudState.currentCloudSongPosition
    val currentCloudSongCount = cloudState.currentCloudSongCount

    val vkMusicDontAsk by cloudViewModel.settings.vkMusicDontAskState.collectAsState()
    val yandexMusicDontAsk by cloudViewModel.settings.yandexMusicDontAskState.collectAsState()
    val youtubeMusicDontAsk by cloudViewModel.settings.youtubeMusicDontAskState.collectAsState()

    CloudSongTextScreenImplContent(
        position = position,
        cloudSongItems = cloudSongItems,
        currentCloudSongPosition = currentCloudSongPosition,
        currentCloudSongCount = currentCloudSongCount,
        listenToMusicVariant = cloudViewModel.settings.listenToMusicVariant,
        showVkDialog = cloudState.showVkDialog,
        showYandexDialog = cloudState.showYandexDialog,
        showYoutubeDialog = cloudState.showYoutubeDialog,
        showWarningDialog = cloudState.showWarningDialog,
        showDeleteDialog = cloudState.showDeleteDialog,
        showChordDialog = cloudState.showChordDialog,
        selectedChord = cloudState.selectedChord,
        vkMusicDontAsk = vkMusicDontAsk,
        yandexMusicDontAsk = yandexMusicDontAsk,
        youtubeMusicDontAsk = youtubeMusicDontAsk,
        submitAction = cloudViewModel::submitAction
    )
}
