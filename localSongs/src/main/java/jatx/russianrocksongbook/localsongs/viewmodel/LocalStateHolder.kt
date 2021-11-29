package jatx.russianrocksongbook.localsongs.viewmodel

import dagger.hilt.android.scopes.ActivityRetainedScoped
import jatx.russianrocksongbook.model.domain.Song
import jatx.russianrocksongbook.viewmodel.CommonStateHolder
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@ActivityRetainedScoped
class LocalStateHolder @Inject constructor(
    val commonStateHolder: CommonStateHolder
) {
    val currentSongCount = MutableStateFlow(0)
    val currentSongList = MutableStateFlow(listOf<Song>())
    val currentSongPosition = MutableStateFlow(0)
    val currentSong: MutableStateFlow<Song?> = MutableStateFlow(null)

    val isEditorMode = MutableStateFlow(false)
    val isAutoPlayMode = MutableStateFlow(false)
    val isUploadButtonEnabled = MutableStateFlow(true)
}