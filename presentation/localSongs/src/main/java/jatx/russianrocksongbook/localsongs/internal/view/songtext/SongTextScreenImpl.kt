package jatx.russianrocksongbook.localsongs.internal.view.songtext

import androidx.compose.runtime.*
import jatx.russianrocksongbook.commonsongtext.view.CommonSongTextScreenImplContent
import jatx.russianrocksongbook.localsongs.internal.viewmodel.LocalViewModel

@Composable
internal fun SongTextScreenImpl(artist: String, position: Int) {
    val localViewModel = LocalViewModel.getInstance()

    val commonSongTextState by localViewModel.commonSongTextStateFlow.collectAsState()

    val song = commonSongTextState.currentSong

    val vkMusicDontAsk by localViewModel.settings.vkMusicDontAskState.collectAsState()
    val yandexMusicDontAsk by localViewModel.settings.yandexMusicDontAskState.collectAsState()
    val youtubeMusicDontAsk by localViewModel.settings.youtubeMusicDontAskState.collectAsState()

    CommonSongTextScreenImplContent(
        artist = artist,
        position = position,
        song = song,
        currentSongPosition = commonSongTextState.currentSongPosition,
        isAutoPlayMode = commonSongTextState.isAutoPlayMode,
        isEditorMode = commonSongTextState.isEditorMode,
        isUploadButtonEnabled = commonSongTextState.isUploadButtonEnabled,
        editorText = localViewModel.editorText,
        scrollSpeed = localViewModel.settings.scrollSpeed,
        listenToMusicVariant = localViewModel.settings.listenToMusicVariant,
        vkMusicDontAsk = vkMusicDontAsk,
        yandexMusicDontAsk = yandexMusicDontAsk,
        youtubeMusicDontAsk = youtubeMusicDontAsk,
        submitAction = localViewModel::submitAction,
        submitEffect = localViewModel::submitEffect
    )
}