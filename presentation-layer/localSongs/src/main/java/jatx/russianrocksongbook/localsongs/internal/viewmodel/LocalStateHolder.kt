package jatx.russianrocksongbook.localsongs.internal.viewmodel

import dagger.hilt.android.scopes.ActivityRetainedScoped
import jatx.russianrocksongbook.domain.models.local.Song
import jatx.russianrocksongbook.viewmodel.CommonStateHolder
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@ActivityRetainedScoped
internal class LocalStateHolder @Inject constructor(
    val commonStateHolder: CommonStateHolder
) {
    val currentSongCount = MutableStateFlow(0)
    val currentSongList = MutableStateFlow(listOf<Song>())
    val currentSongPosition = MutableStateFlow(0)
    val currentSong: MutableStateFlow<Song?> = MutableStateFlow(null)

    val isEditorMode = MutableStateFlow(false)
    val isAutoPlayMode = MutableStateFlow(false)
    val isUploadButtonEnabled = MutableStateFlow(true)

    val scrollPosition = MutableStateFlow(0)
    val isLastOrientationPortrait = MutableStateFlow(true)
    val wasOrientationChanged = MutableStateFlow(false)
    val needScroll = MutableStateFlow(false)

    val drawerState = MutableStateFlow<DrawerState>(DrawerStateClosed)
}