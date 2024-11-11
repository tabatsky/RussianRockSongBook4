package jatx.russianrocksongbook.localsongs.internal.view.songtext

import androidx.compose.runtime.*
import jatx.russianrocksongbook.commonview.songtext.CommonSongTextScreenImplContent
import jatx.russianrocksongbook.localsongs.internal.viewmodel.LocalViewModel

@Composable
internal fun SongTextScreenImpl(artist: String, position: Int) {
    val localViewModel = LocalViewModel.getInstance()

    val localState by localViewModel.localStateFlow.collectAsState()

    val song = localState.currentSong

    val vkMusicDontAsk by localViewModel.settings.vkMusicDontAskState.collectAsState()
    val yandexMusicDontAsk by localViewModel.settings.yandexMusicDontAskState.collectAsState()
    val youtubeMusicDontAsk by localViewModel.settings.youtubeMusicDontAskState.collectAsState()

    CommonSongTextScreenImplContent(
        artist = artist,
        position = position,
        song = song,
        currentSongPosition = localState.currentSongPosition,
        isAutoPlayMode = localState.isAutoPlayMode,
        isEditorMode = localState.isEditorMode,
        isUploadButtonEnabled = localState.isUploadButtonEnabled,
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