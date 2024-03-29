package jatx.russianrocksongbook.cloudsongs.internal.view.cloudsongtext

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import jatx.russianrocksongbook.domain.repository.preferences.ListenToMusicVariant
import jatx.russianrocksongbook.domain.repository.preferences.Theme

@Composable
internal fun CloudSongTextPanel(
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

    @Composable
    fun TheContent() {
        CloudSongTextPanelContent(
            W = W,
            H = H,
            theme = theme,
            listenToMusicVariant = listenToMusicVariant,
            onYandexMusicClick = onYandexMusicClick,
            onVkMusicClick = onVkMusicClick,
            onYoutubeMusicClick = onYoutubeMusicClick,
            onDownloadClick = onDownloadClick,
            onWarningClick = onWarningClick,
            onLikeClick = onLikeClick,
            onDislikeClick = onDislikeClick,
            onDislikeLongClick = onDislikeLongClick
        )
    }

    if (W < H) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .height(A)
                .background(theme.colorBg),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TheContent()
        }
    } else {
        Column (
            modifier = Modifier
                .fillMaxHeight()
                .width(A)
                .background(theme.colorBg),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            TheContent()
        }
    }
}