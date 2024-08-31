package jatx.russianrocksongbook.textsearch.internal.view.textsearchsongtext

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import jatx.russianrocksongbook.commonview.buttons.CommonIconButton
import jatx.russianrocksongbook.testing.ADD_TO_FAVORITE_BUTTON
import jatx.russianrocksongbook.testing.DELETE_FROM_FAVORITE_BUTTON
import jatx.russianrocksongbook.testing.LEFT_BUTTON
import jatx.russianrocksongbook.testing.RIGHT_BUTTON
import jatx.russianrocksongbook.textsearch.R
import jatx.russianrocksongbook.textsearch.internal.viewmodel.NextSong
import jatx.russianrocksongbook.textsearch.internal.viewmodel.PrevSong
import jatx.russianrocksongbook.textsearch.internal.viewmodel.SetAutoPlayMode
import jatx.russianrocksongbook.textsearch.internal.viewmodel.SetFavorite
import jatx.russianrocksongbook.textsearch.internal.viewmodel.TextSearchViewModel

@Composable
internal fun TextSearchSongTextActions(
    isFavorite: Boolean,
    onSongChanged: () -> Unit
) {
    val textSearchViewModel = TextSearchViewModel.getInstance()

    val textSearchState by textSearchViewModel.textSearchStateFlow.collectAsState()

    if (textSearchState.isAutoPlayMode) {
        val onPauseClick = {
            textSearchViewModel.submitAction(SetAutoPlayMode(false))
        }
        CommonIconButton(
            resId = R.drawable.ic_pause,
            onClick = onPauseClick
        )
    } else {
        val isEditorMode = textSearchState.isEditorMode
        val onPlayClick = {
            if (!isEditorMode) {
                textSearchViewModel.submitAction(SetAutoPlayMode(true))
            }
        }
        CommonIconButton(
            resId = R.drawable.ic_play,
            onClick = onPlayClick
        )
    }
    val onLeftClick =  {
        textSearchViewModel.submitAction(PrevSong)
        onSongChanged()
    }
    CommonIconButton(
        resId = R.drawable.ic_left,
        testTag = LEFT_BUTTON,
        onClick = onLeftClick
    )
    if (isFavorite) {
        val onDeleteClick = {
            textSearchViewModel.submitAction(SetFavorite(false))
        }
        CommonIconButton(
            resId = R.drawable.ic_delete,
            testTag = DELETE_FROM_FAVORITE_BUTTON,
            onClick = onDeleteClick
        )
    } else {
        val onStarClick = {
            textSearchViewModel.submitAction(SetFavorite(true))
        }
        CommonIconButton(
            resId = R.drawable.ic_star,
            testTag = ADD_TO_FAVORITE_BUTTON,
            onClick = onStarClick
        )
    }
    val onRightClick = {
        textSearchViewModel.submitAction(NextSong)
        onSongChanged()
    }
    CommonIconButton(
        resId = R.drawable.ic_right,
        testTag = RIGHT_BUTTON,
        onClick = onRightClick
    )
}
