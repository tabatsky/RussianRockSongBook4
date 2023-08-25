package jatx.russianrocksongbook.localsongs.internal.viewmodel

import jatx.russianrocksongbook.commonviewmodel.CommonUIState
import jatx.russianrocksongbook.commonviewmodel.UIState
import jatx.russianrocksongbook.domain.models.local.Song
import jatx.russianrocksongbook.navigation.ScreenVariant

data class LocalUIState(
    val commonUIState: CommonUIState,
    val currentSongCount: Int,
    val currentSongList: List<Song>,
    val currentSongPosition: Int,
    val currentSong: Song?,
    val isEditorMode: Boolean,
    val isAutoPlayMode: Boolean,
    val isUploadButtonEnabled: Boolean,
    val scrollPosition: Int,
    val needScroll: Boolean
): UIState {
    companion object {
        fun initial(commonUIState: CommonUIState) = LocalUIState(
            commonUIState = commonUIState,
            currentSongCount = 0,
            currentSongList = listOf(),
            currentSongPosition = 0,
            currentSong = null,
            isEditorMode = false,
            isAutoPlayMode = false,
            isUploadButtonEnabled = true,
            scrollPosition = 0,
            needScroll = false
        )
    }

    val currentArtist: String
        get() = commonUIState.currentArtist

    val artistList: List<String>
        get() = commonUIState.artistList

    val currentScreenVariant: ScreenVariant
        get() = commonUIState.currentScreenVariant
}