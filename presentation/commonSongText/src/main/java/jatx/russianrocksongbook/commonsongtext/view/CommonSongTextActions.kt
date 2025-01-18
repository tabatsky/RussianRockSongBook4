package jatx.russianrocksongbook.commonsongtext.view

import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import jatx.russianrocksongbook.commonsongtext.R
import jatx.russianrocksongbook.commonsongtext.viewmodel.NextSong
import jatx.russianrocksongbook.commonsongtext.viewmodel.PrevSong
import jatx.russianrocksongbook.commonsongtext.viewmodel.SetAutoPlayMode
import jatx.russianrocksongbook.commonsongtext.viewmodel.SetFavorite
import jatx.russianrocksongbook.commonview.buttons.CommonIconButton
import jatx.russianrocksongbook.commonviewmodel.UIAction
import jatx.russianrocksongbook.testing.ADD_TO_FAVORITE_BUTTON
import jatx.russianrocksongbook.testing.DELETE_FROM_FAVORITE_BUTTON
import jatx.russianrocksongbook.testing.LEFT_BUTTON
import jatx.russianrocksongbook.testing.RIGHT_BUTTON

@Composable
internal fun CommonSongTextActions(
    isFavorite: Boolean,
    onSongChanged: () -> Unit,
    isAutoPlayMode: Boolean,
    isEditorMode: Boolean,
    submitAction: (UIAction) -> Unit
) {
    Crossfade(
        targetState = isAutoPlayMode,
        label = "autoPlay",
    ) { autoPlay ->
        if (autoPlay) {
            val onPauseClick = {
                submitAction(SetAutoPlayMode(false))
            }
            CommonIconButton(
                resId = R.drawable.ic_pause,
                onClick = onPauseClick
            )
        } else {
            val onPlayClick = {
                if (!isEditorMode) {
                    submitAction(SetAutoPlayMode(true))
                }
            }
            CommonIconButton(
                resId = R.drawable.ic_play,
                onClick = onPlayClick
            )
        }
    }
    val onLeftClick =  {
        submitAction(PrevSong)
        onSongChanged()
    }
    CommonIconButton(
        resId = R.drawable.ic_left,
        testTag = LEFT_BUTTON,
        onClick = onLeftClick
    )
    Crossfade(
        targetState = isFavorite,
        label = "favorite",
    ) { favorite ->
        if (favorite) {
            val onDeleteClick = {
                submitAction(SetFavorite(false))
            }
            CommonIconButton(
                resId = R.drawable.ic_delete,
                testTag = DELETE_FROM_FAVORITE_BUTTON,
                onClick = onDeleteClick
            )
        } else {
            val onStarClick = {
                submitAction(SetFavorite(true))
            }
            CommonIconButton(
                resId = R.drawable.ic_star,
                testTag = ADD_TO_FAVORITE_BUTTON,
                onClick = onStarClick
            )
        }
    }
    val onRightClick = {
        submitAction(NextSong)
        onSongChanged()
    }
    CommonIconButton(
        resId = R.drawable.ic_right,
        testTag = RIGHT_BUTTON,
        onClick = onRightClick
    )
}
