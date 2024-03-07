package jatx.russianrocksongbook.localsongs.internal.view.songtext

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import jatx.russianrocksongbook.commonview.buttons.CommonIconButton
import jatx.russianrocksongbook.localsongs.R
import jatx.russianrocksongbook.localsongs.internal.viewmodel.LocalViewModel
import jatx.russianrocksongbook.localsongs.internal.viewmodel.NextSong
import jatx.russianrocksongbook.localsongs.internal.viewmodel.PrevSong
import jatx.russianrocksongbook.localsongs.internal.viewmodel.SetAutoPlayMode
import jatx.russianrocksongbook.localsongs.internal.viewmodel.SetFavorite
import jatx.russianrocksongbook.testing.ADD_TO_FAVORITE_BUTTON
import jatx.russianrocksongbook.testing.DELETE_FROM_FAVORITE_BUTTON
import jatx.russianrocksongbook.testing.LEFT_BUTTON
import jatx.russianrocksongbook.testing.RIGHT_BUTTON

@Composable
internal fun SongTextActions(
    isFavorite: Boolean,
    onSongChanged: () -> Unit
) {
    val localViewModel = LocalViewModel.getInstance()

    val localState by localViewModel.localState.collectAsState()

    if (localState.isAutoPlayMode) {
        val onPauseClick = {
            localViewModel.submitAction(SetAutoPlayMode(false))
        }
        CommonIconButton(
            resId = R.drawable.ic_pause,
            onClick = onPauseClick
        )
    } else {
        val isEditorMode = localState.isEditorMode
        val onPlayClick = {
            if (!isEditorMode) {
                localViewModel.submitAction(SetAutoPlayMode(true))
            }
        }
        CommonIconButton(
            resId = R.drawable.ic_play,
            onClick = onPlayClick
        )
    }
    val onLeftClick =  {
        localViewModel.submitAction(PrevSong)
        onSongChanged()
    }
    CommonIconButton(
        resId = R.drawable.ic_left,
        testTag = LEFT_BUTTON,
        onClick = onLeftClick
    )
    if (isFavorite) {
        val onDeleteClick = {
            localViewModel.submitAction(SetFavorite(false))
        }
        CommonIconButton(
            resId = R.drawable.ic_delete,
            testTag = DELETE_FROM_FAVORITE_BUTTON,
            onClick = onDeleteClick
        )
    } else {
        val onStarClick = {
            localViewModel.submitAction(SetFavorite(true))
        }
        CommonIconButton(
            resId = R.drawable.ic_star,
            testTag = ADD_TO_FAVORITE_BUTTON,
            onClick = onStarClick
        )
    }
    val onRightClick = {
        localViewModel.submitAction(NextSong)
        onSongChanged()
    }
    CommonIconButton(
        resId = R.drawable.ic_right,
        testTag = RIGHT_BUTTON,
        onClick = onRightClick
    )
}
