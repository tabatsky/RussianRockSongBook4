package jatx.russianrocksongbook.commonview.songtext

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import jatx.russianrocksongbook.commonview.buttons.EditButton
import jatx.russianrocksongbook.commonview.buttons.SaveButton
import jatx.russianrocksongbook.commonview.buttons.TrashButton
import jatx.russianrocksongbook.commonview.buttons.UploadButton
import jatx.russianrocksongbook.commonview.buttons.VkMusicButton
import jatx.russianrocksongbook.commonview.buttons.WarningButton
import jatx.russianrocksongbook.commonview.buttons.YandexMusicButton
import jatx.russianrocksongbook.commonview.buttons.YoutubeMusicButton
import jatx.russianrocksongbook.domain.repository.preferences.ListenToMusicVariant
import jatx.russianrocksongbook.domain.repository.preferences.Theme

@Composable
internal fun CommonSongTextPanelContent(
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
    UploadButton(
        size = A,
        theme = theme,
        onClick = onUploadClick
    )
    WarningButton(
        size = A,
        theme = theme,
        onClick = onWarningClick
    )
    TrashButton(
        size = A,
        theme = theme,
        onClick = onTrashClick
    )
    if (isEditorMode) {
        SaveButton(
            size = A,
            theme = theme,
            onClick = onSaveClick
        )
    } else {
        EditButton(
            size = A,
            theme = theme,
            onClick = onEditClick
        )
    }
}