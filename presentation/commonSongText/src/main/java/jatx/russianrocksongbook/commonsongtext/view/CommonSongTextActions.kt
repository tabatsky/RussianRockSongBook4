package jatx.russianrocksongbook.commonsongtext.view

import androidx.compose.runtime.Composable
import jatx.russianrocksongbook.commonsongtext.R
import jatx.russianrocksongbook.commonview.buttons.CommonIconButton
import jatx.russianrocksongbook.commonviewmodel.NextSong
import jatx.russianrocksongbook.commonviewmodel.PrevSong
import jatx.russianrocksongbook.commonviewmodel.SetAutoPlayMode
import jatx.russianrocksongbook.commonviewmodel.SetFavorite
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
    if (isAutoPlayMode) {
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
    val onLeftClick =  {
        submitAction(PrevSong)
        onSongChanged()
    }
    CommonIconButton(
        resId = R.drawable.ic_left,
        testTag = LEFT_BUTTON,
        onClick = onLeftClick
    )
    if (isFavorite) {
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
