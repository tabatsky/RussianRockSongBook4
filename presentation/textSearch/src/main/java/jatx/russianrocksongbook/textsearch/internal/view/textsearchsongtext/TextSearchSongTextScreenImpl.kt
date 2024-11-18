package jatx.russianrocksongbook.textsearch.internal.view.textsearchsongtext

import androidx.compose.runtime.*
import jatx.russianrocksongbook.commonsongtext.view.CommonSongTextScreenImplContent
import jatx.russianrocksongbook.textsearch.internal.viewmodel.TextSearchViewModel

@Composable
internal fun TextSearchSongTextScreenImpl(position: Int) {
    val textSearchViewModel = TextSearchViewModel.getInstance()
    val textSearchState by textSearchViewModel.textSearchStateFlow.collectAsState()

    val song = textSearchState.currentSong

    val vkMusicDontAsk by textSearchViewModel.settings.vkMusicDontAskState.collectAsState()
    val yandexMusicDontAsk by textSearchViewModel.settings.yandexMusicDontAskState.collectAsState()
    val youtubeMusicDontAsk by textSearchViewModel.settings.youtubeMusicDontAskState.collectAsState()

    CommonSongTextScreenImplContent(
        artist = song?.artist ?: "",
        position = position,
        song = song,
        currentSongPosition = textSearchState.currentSongPosition,
        isAutoPlayMode = textSearchState.isAutoPlayMode,
        isEditorMode = textSearchState.isEditorMode,
        isUploadButtonEnabled = textSearchState.isUploadButtonEnabled,
        editorText = textSearchViewModel.editorText,
        scrollSpeed = textSearchViewModel.settings.scrollSpeed,
        listenToMusicVariant = textSearchViewModel.settings.listenToMusicVariant,
        vkMusicDontAsk = vkMusicDontAsk,
        yandexMusicDontAsk = yandexMusicDontAsk,
        youtubeMusicDontAsk = youtubeMusicDontAsk,
        submitAction = textSearchViewModel::submitAction,
        submitEffect = textSearchViewModel::submitEffect
    )
}