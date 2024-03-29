package jatx.russianrocksongbook.cloudsongs.internal.view.cloudsongtext

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import jatx.russianrocksongbook.commonview.buttons.*
import jatx.russianrocksongbook.domain.repository.preferences.ListenToMusicVariant
import jatx.russianrocksongbook.domain.repository.preferences.Theme

@Composable
internal fun CloudSongTextPanelContent(
    W: Dp,
    H: Dp,
    theme: Theme,
    listenToMusicVariant: ListenToMusicVariant,
    onYandexMusicClick: () -> Unit,
    onVkMusicClick: () -> Unit,
    onYoutubeMusicClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onWarningClick: () -> Unit,
    onLikeClick: () -> Unit,
    onDislikeClick: () -> Unit,
    onDislikeLongClick: () -> Unit
) {
    val A = if (W < H) W * 3.0f / 21 else H * 3.0f / 21

    if (listenToMusicVariant.isYandex) {
        YandexMusicButton(
            size = A,
            theme = theme,
            onClick = onYandexMusicClick
        )
    }
    if (listenToMusicVariant.isVk) {
        VkMusicButton(
            size = A,
            theme = theme,
            onClick = onVkMusicClick
        )
    }
    if (listenToMusicVariant.isYoutube) {
        YoutubeMusicButton(
            size = A,
            theme = theme,
            onClick = onYoutubeMusicClick
        )
    }
    DownloadButton(
        size = A,
        theme = theme,
        onClick = onDownloadClick
    )
    WarningButton(
        size = A,
        theme = theme,
        onClick = onWarningClick
    )
    LikeButton(
        size = A,
        theme = theme,
        onClick = onLikeClick
    )
    DislikeButton(
        size = A,
        theme = theme,
        onClick = onDislikeClick,
        onLongClick = onDislikeLongClick
    )
}
