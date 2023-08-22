package jatx.russianrocksongbook.localsongs.internal.view.songtext

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import jatx.russianrocksongbook.domain.repository.preferences.ListenToMusicVariant
import jatx.russianrocksongbook.domain.repository.preferences.Theme

@Composable
internal fun SongTextPanel(
    W: Dp,
    H: Dp,
    theme: Theme,
    isEditorMode: Boolean,
    listenToMusicVariant: ListenToMusicVariant,
    onYandexMusicClick: () -> Unit,
    onVkMusicClick: () -> Unit,
    onYoutubeMusicClick: () -> Unit,
    onUploadClick: () -> Unit,
    onWarningClick: () -> Unit,
    onTrashClick: () -> Unit,
    onEditClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    val A = if (W < H) W * 3.0f / 21 else H * 3.0f / 21

    @Composable
    fun TheContent() {
        SongTextPanelContent(
            W = W,
            H = H,
            theme = theme,
            isEditorMode = isEditorMode,
            listenToMusicVariant = listenToMusicVariant,
            onYandexMusicClick = onYandexMusicClick,
            onVkMusicClick = onVkMusicClick,
            onYoutubeMusicClick = onYoutubeMusicClick,
            onUploadClick = onUploadClick,
            onWarningClick = onWarningClick,
            onTrashClick = onTrashClick,
            onEditClick = onEditClick,
            onSaveClick = onSaveClick
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