package jatx.russianrocksongbook.localsongs.internal.view.songtext

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
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
        CommonIconButton(
            resId = R.drawable.ic_pause,
        ) {
            localViewModel.setAutoPlayMode(false)
        }
    } else {
        val isEditorMode = localViewModel.isEditorMode.collectAsState().value
        CommonIconButton(
            resId = R.drawable.ic_play,
        ) {
            if (!isEditorMode) {
                localViewModel.setAutoPlayMode(true)
            }
        }
    }
    CommonIconButton(
        resId = R.drawable.ic_left,
        testTag = LEFT_BUTTON
    ) {
        localViewModel.prevSong()
        onSongChanged()
    }
    if (isFavorite) {
        CommonIconButton(
            resId = R.drawable.ic_delete,
            testTag = DELETE_FROM_FAVORITE_BUTTON
        ) {
            localViewModel.setFavorite(false)
        }
    } else {
        CommonIconButton(
            resId = R.drawable.ic_star,
            testTag = ADD_TO_FAVORITE_BUTTON
        ) {
            localViewModel.setFavorite(true)
        }
    }
    CommonIconButton(
        resId = R.drawable.ic_right,
        testTag = RIGHT_BUTTON
    ) {
        localViewModel.nextSong()
        onSongChanged()
    }
}
