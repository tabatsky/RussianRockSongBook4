package jatx.russianrocksongbook.textsearch.internal.view.textsearchsongtext

import androidx.compose.runtime.*
import jatx.russianrocksongbook.commonview.songtext.CommonSongTextScreenImpl
import jatx.russianrocksongbook.textsearch.internal.viewmodel.TextSearchViewModel

@Composable
internal fun TextSearchSongTextScreenImpl(position: Int) {
    val textSearchViewModel = TextSearchViewModel.getInstance()
    val textSearchState by textSearchViewModel.textSearchStateFlow.collectAsState()

    val song = textSearchState.currentSong

    CommonSongTextScreenImpl(
        artist = song?.artist ?: "",
        position = position,
        song = song,
        currentSongPosition = textSearchState.currentSongPosition,
        isAutoPlayMode = textSearchState.isAutoPlayMode,
        isEditorMode = textSearchState.isEditorMode,
        isUploadButtonEnabled = textSearchState.isUploadButtonEnabled,
        editorText = textSearchViewModel.editorText,
        commonViewModel = textSearchViewModel,
        submitAction = textSearchViewModel::submitAction,
        submitEffect = textSearchViewModel::submitEffect
    )
}