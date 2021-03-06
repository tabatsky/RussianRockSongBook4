package jatx.russianrocksongbook.commonview.buttons

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import jatx.russianrocksongbook.commonview.R
import jatx.russianrocksongbook.domain.repository.preferences.Theme
import jatx.russianrocksongbook.testing.*

@Composable
fun YandexMusicButton(size: Dp, theme: Theme, onClick: () -> Unit) =
    FooterButton(
        size = size,
        theme = theme,
        resId = R.drawable.ic_yandex,
        testTag = MUSIC_BUTTON,
        onClick = onClick
    )

@Composable
fun VkMusicButton(size: Dp, theme: Theme, onClick: () -> Unit) =
    FooterButton(
        size = size,
        theme = theme,
        resId = R.drawable.ic_vk,
        testTag = MUSIC_BUTTON,
        onClick = onClick
    )

@Composable
fun YoutubeMusicButton(size: Dp, theme: Theme, onClick: () -> Unit) =
    FooterButton(
        size = size,
        theme = theme,
        resId = R.drawable.ic_youtube,
        testTag = MUSIC_BUTTON,
        onClick = onClick
    )

@Composable
fun UploadButton(size: Dp, theme: Theme, onClick: () -> Unit) =
    FooterButton(
        size = size,
        theme = theme,
        resId = R.drawable.ic_upload,
        testTag = UPLOAD_BUTTON,
        onClick = onClick
    )

@Composable
fun WarningButton(size: Dp, theme: Theme, onClick: () -> Unit)  =
    FooterButton(
        size = size,
        theme = theme,
        resId = R.drawable.ic_warning,
        testTag = WARNING_BUTTON,
        onClick = onClick
    )

@Composable
fun TrashButton(size: Dp, theme: Theme, onClick: () -> Unit)  =
    FooterButton(
        size = size,
        theme = theme,
        resId = R.drawable.ic_trash,
        testTag = TRASH_BUTTON,
        onClick = onClick
    )

@Composable
fun EditButton(size: Dp, theme: Theme, onClick: () -> Unit)  =
    FooterButton(
        size = size,
        theme = theme,
        resId = R.drawable.ic_edit,
        testTag = EDIT_BUTTON,
        onClick = onClick
    )

@Composable
fun SaveButton(size: Dp, theme: Theme, onClick: () -> Unit)  =
    FooterButton(
        size = size,
        theme = theme,
        resId = R.drawable.ic_save,
        testTag = SAVE_BUTTON,
        onClick = onClick
    )

@Composable
fun DownloadButton(size: Dp, theme: Theme, onClick: () -> Unit)  =
    FooterButton(
        size = size,
        theme = theme,
        resId = R.drawable.ic_download,
        testTag = DOWNLOAD_BUTTON,
        onClick = onClick
    )

@Composable
fun LikeButton(size: Dp, theme: Theme, onClick: () -> Unit)  =
    FooterButton(
        size = size,
        theme = theme,
        resId = R.drawable.ic_like,
        testTag = LIKE_BUTTON,
        onClick = onClick
    )

@Composable
fun DislikeButton(
    size: Dp,
    theme: Theme,
    onClick: () -> Unit,
    onLongClick: () -> Unit
)  =
    FooterButton(
        size = size,
        theme = theme,
        resId = R.drawable.ic_dislike,
        testTag = DISLIKE_BUTTON,
        onClick = onClick,
        onLongClick = onLongClick
    )

@Composable
private fun FooterButton(
    size: Dp,
    theme: Theme,
    @DrawableRes resId: Int,
    testTag: String? = null,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {}
) {
    val modifier = testTag?.let {
        Modifier.testTag(it)
    } ?: Modifier
    Box(
        modifier = modifier
            .background(theme.colorCommon)
            .width(size)
            .height(size)
            .combinedClickable(
                onClick = { onClick() },
                onLongClick = { onLongClick() }
            )
    ) {
        Icon(
            painter = painterResource(id = resId),
            contentDescription = "",
            modifier = Modifier.padding(10.dp)
        )
    }
}
