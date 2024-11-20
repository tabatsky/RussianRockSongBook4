package jatx.russianrocksongbook.cloudsongs.internal.view.cloudsongtext

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import jatx.russianrocksongbook.commonview.theme.DarkTheme
import jatx.russianrocksongbook.domain.models.cloud.CloudSong
import jatx.russianrocksongbook.domain.repository.preferences.ListenToMusicVariant
import kotlinx.coroutines.flow.flowOf

@Preview
@Composable
fun CloudSongTextScreenImplPreviewPortraitDark() {
    val cloudSongs = (1..30)
        .map {
            CloudSong(
                artist = "Исполнитель $it",
                title = "Название $it",
                text = "Текст текст\nТекст\nAm Em\nТекст\n",
                variant = 1,
                likeCount = 2,
                dislikeCount = 1
            )
        }

    val cloudSongItems = flowOf(PagingData.from(cloudSongs)).collectAsLazyPagingItems()

    DarkTheme {
        CloudSongTextScreenImplContent(
            position = 3,
            cloudSongItems = cloudSongItems,
            listenToMusicVariant = ListenToMusicVariant.YANDEX_AND_VK,
            vkMusicDontAsk = false,
            yandexMusicDontAsk = false,
            youtubeMusicDontAsk = false,
            submitAction = {}
        )
    }
}
