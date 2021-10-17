package jatx.russianrocksongbook.view

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import jatx.russianrocksongbook.R
import jatx.russianrocksongbook.data.Theme

@Composable
fun FooterButton(
    size: Dp,
    theme: Theme,
    @DrawableRes resId: Int,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .background(theme.colorCommon)
            .width(size)
            .height(size)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onClick() },
                    onLongPress = { onLongClick() }
                )
            }
    ) {
        Icon(
            painter = painterResource(id = resId),
            contentDescription = "",
            modifier = Modifier.padding(10.dp)
        )
    }
}


@Composable
fun VkMusicButton(size: Dp, theme: Theme, onClick: () -> Unit) =
    FooterButton(
        size = size,
        theme = theme,
        resId = R.drawable.ic_vk,
        onClick = onClick
    )

@Composable
fun YoutubeMusicButton(size: Dp, theme: Theme, onClick: () -> Unit) =
    FooterButton(
        size = size,
        theme = theme,
        resId = R.drawable.ic_youtube,
        onClick = onClick
    )

@Composable
fun UploadButton(size: Dp, theme: Theme, onClick: () -> Unit) =
    FooterButton(
        size = size,
        theme = theme,
        resId = R.drawable.ic_upload,
        onClick = onClick
    )

@Composable
fun WarningButton(size: Dp, theme: Theme, onClick: () -> Unit)  =
    FooterButton(
        size = size,
        theme = theme,
        resId = R.drawable.ic_warning,
        onClick = onClick
    )

@Composable
fun TrashButton(size: Dp, theme: Theme, onClick: () -> Unit)  =
    FooterButton(
        size = size,
        theme = theme,
        resId = R.drawable.ic_trash,
        onClick = onClick
    )

@Composable
fun EditButton(size: Dp, theme: Theme, onClick: () -> Unit)  =
    FooterButton(
        size = size,
        theme = theme,
        resId = R.drawable.ic_edit,
        onClick = onClick
    )

@Composable
fun SaveButton(size: Dp, theme: Theme, onClick: () -> Unit)  =
    FooterButton(
        size = size,
        theme = theme,
        resId = R.drawable.ic_save,
        onClick = onClick
    )

@Composable
fun DownloadButton(size: Dp, theme: Theme, onClick: () -> Unit)  =
    FooterButton(
        size = size,
        theme = theme,
        resId = R.drawable.ic_download,
        onClick = onClick
    )

@Composable
fun LikeButton(size: Dp, theme: Theme, onClick: () -> Unit)  =
    FooterButton(
        size = size,
        theme = theme,
        resId = R.drawable.ic_like,
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
        onClick = onClick,
        onLongClick = onLongClick
    )