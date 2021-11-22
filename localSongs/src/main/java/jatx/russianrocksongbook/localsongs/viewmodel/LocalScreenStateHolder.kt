package jatx.russianrocksongbook.localsongs.viewmodel

import jatx.russianrocksongbook.model.domain.Song
import jatx.russianrocksongbook.viewmodel.ScreenStateHolder
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalScreenStateHolder @Inject constructor(
    val screenStateHolder: ScreenStateHolder
) {
    val currentSongCount = MutableStateFlow(0)
    val currentSongList = MutableStateFlow(listOf<Song>())
    val currentSongPosition = MutableStateFlow(0)
    val currentSong: MutableStateFlow<Song?> = MutableStateFlow(null)

    val isEditorMode = MutableStateFlow(false)
    val isAutoPlayMode = MutableStateFlow(false)
    val isUploadButtonEnabled = MutableStateFlow(true)
}