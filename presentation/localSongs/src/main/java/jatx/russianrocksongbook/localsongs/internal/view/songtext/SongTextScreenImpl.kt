package jatx.russianrocksongbook.localsongs.internal.view.songtext

import androidx.compose.runtime.*
import jatx.russianrocksongbook.commonview.songtext.CommonSongTextScreenImpl
import jatx.russianrocksongbook.localsongs.internal.viewmodel.LocalViewModel

@Composable
internal fun SongTextScreenImpl(artist: String, position: Int) {
    val localViewModel = LocalViewModel.getInstance()

    val localState by localViewModel.localStateFlow.collectAsState()

    val song = localState.currentSong

    CommonSongTextScreenImpl(
        artist = artist,
        position = position,
        song = song,
        currentSongPosition = localState.currentSongPosition,
        isAutoPlayMode = localState.isAutoPlayMode,
        isEditorMode = localState.isEditorMode,
        isUploadButtonEnabled = localState.isUploadButtonEnabled,
        editorText = localViewModel.editorText,
        commonViewModel = localViewModel,
        submitAction = localViewModel::submitAction,
        submitEffect = localViewModel::submitEffect
    )
}