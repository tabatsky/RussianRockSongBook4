package jatx.russianrocksongbook.textsearch.internal.view.textsearchsongtext

import androidx.compose.runtime.*
import jatx.russianrocksongbook.commonsongtext.view.CommonSongTextScreenImplContent
import jatx.russianrocksongbook.textsearch.internal.viewmodel.TextSearchViewModel

@Composable
internal fun TextSearchSongTextScreenImpl(position: Int, randomKey: Int) {
    val textSearchViewModel = TextSearchViewModel.getInstance()
    val commonSongTextState by textSearchViewModel.commonSongTextStateFlow.collectAsState()

    val song = commonSongTextState.currentSong

    val vkMusicDontAsk by textSearchViewModel.settings.vkMusicDontAskState.collectAsState()
    val yandexMusicDontAsk by textSearchViewModel.settings.yandexMusicDontAskState.collectAsState()
    val youtubeMusicDontAsk by textSearchViewModel.settings.youtubeMusicDontAskState.collectAsState()

    CommonSongTextScreenImplContent(
        position = position,
        randomKey = randomKey,
        song = song,
        songCount = commonSongTextState.currentSongCount,
        lastRandomKey = commonSongTextState.lastRandomKey,
        currentSongPosition = commonSongTextState.currentSongPosition,
        isAutoPlayMode = commonSongTextState.isAutoPlayMode,
        isEditorMode = commonSongTextState.isEditorMode,
        isUploadButtonEnabled = commonSongTextState.isUploadButtonEnabled,
        editorText = textSearchViewModel.editorText,
        scrollSpeed = textSearchViewModel.settings.scrollSpeed,
        listenToMusicVariant = textSearchViewModel.settings.listenToMusicVariant,
        showVkDialog = commonSongTextState.showVkDialog,
        showYandexDialog = commonSongTextState.showYandexDialog,
        showYoutubeDialog = commonSongTextState.showYoutubeDialog,
        showUploadDialog = commonSongTextState.showUploadDialog,
        showDeleteToTrashDialog = commonSongTextState.showDeleteToTrashDialog,
        showWarningDialog = commonSongTextState.showWarningDialog,
        showChordDialog = commonSongTextState.showChordDialog,
        selectedChord = commonSongTextState.selectedChord,
        vkMusicDontAsk = vkMusicDontAsk,
        yandexMusicDontAsk = yandexMusicDontAsk,
        youtubeMusicDontAsk = youtubeMusicDontAsk,
        submitAction = textSearchViewModel::submitAction,
        submitEffect = textSearchViewModel::submitEffect
    )
}