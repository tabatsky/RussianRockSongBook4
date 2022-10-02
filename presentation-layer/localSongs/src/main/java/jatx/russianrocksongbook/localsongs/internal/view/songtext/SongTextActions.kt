package jatx.russianrocksongbook.localsongs.internal.view.songtext

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import jatx.dpad.dpadFocusable
import jatx.russianrocksongbook.commonview.buttons.CommonIconButton
import jatx.russianrocksongbook.localsongs.R
import jatx.russianrocksongbook.localsongs.internal.viewmodel.LocalViewModel
import jatx.russianrocksongbook.testing.ADD_TO_FAVORITE_BUTTON
import jatx.russianrocksongbook.testing.DELETE_FROM_FAVORITE_BUTTON
import jatx.russianrocksongbook.testing.LEFT_BUTTON
import jatx.russianrocksongbook.testing.RIGHT_BUTTON

@Composable
internal fun SongTextActions(
    isFavorite: Boolean,
    onSongChanged: () -> Unit
) {
    val localViewModel: LocalViewModel = viewModel()

    if (localViewModel.isAutoPlayMode.collectAsState().value) {
        val onPauseClick = {
            localViewModel.setAutoPlayMode(false)
        }
        CommonIconButton(
            resId = R.drawable.ic_pause,
            modifier = Modifier
                .dpadFocusable(onClick = onPauseClick),
            onClick = onPauseClick
        )
    } else {
        val isEditorMode = localViewModel.isEditorMode.collectAsState().value
        val onPlayClick = {
            if (!isEditorMode) {
                localViewModel.setAutoPlayMode(true)
            }
        }
        CommonIconButton(
            resId = R.drawable.ic_play,
            modifier = Modifier
                .dpadFocusable(onClick = onPlayClick),
            onClick = onPlayClick
        )
    }
    val onLeftClick =  {
        localViewModel.prevSong()
        onSongChanged()
    }
    CommonIconButton(
        resId = R.drawable.ic_left,
        testTag = LEFT_BUTTON,
        modifier = Modifier
            .dpadFocusable(onClick = onLeftClick),
        onClick = onLeftClick
    )
    if (isFavorite) {
        val onDeleteClick = {
            localViewModel.setFavorite(false)
        }
        CommonIconButton(
            resId = R.drawable.ic_delete,
            testTag = DELETE_FROM_FAVORITE_BUTTON,
            modifier = Modifier
                .dpadFocusable(onClick = onDeleteClick),
            onClick = onDeleteClick
        )
    } else {
        val onStarClick = {
            localViewModel.setFavorite(true)
        }
        CommonIconButton(
            resId = R.drawable.ic_star,
            testTag = ADD_TO_FAVORITE_BUTTON,
            modifier = Modifier
                .dpadFocusable(onClick = onStarClick),
            onClick = onStarClick
        )
    }
    val onRightClick = {
        localViewModel.nextSong()
        onSongChanged()
    }
    CommonIconButton(
        resId = R.drawable.ic_right,
        testTag = RIGHT_BUTTON,
        modifier = Modifier
            .dpadFocusable(onClick = onRightClick),
        onClick = onRightClick
    )
}
