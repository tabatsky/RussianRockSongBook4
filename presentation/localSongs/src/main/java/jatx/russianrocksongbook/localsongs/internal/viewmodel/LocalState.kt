package jatx.russianrocksongbook.localsongs.internal.viewmodel

import jatx.russianrocksongbook.commonviewmodel.CommonState
import jatx.russianrocksongbook.domain.models.local.Song
import jatx.russianrocksongbook.navigation.ScreenVariant

data class LocalState(
    val commonState: CommonState,
    val currentSongCount: Int,
    val currentSongList: List<Song>,
    val currentSongPosition: Int,
    val currentSong: Song?,
    val isEditorMode: Boolean,
    val isAutoPlayMode: Boolean,
    val isUploadButtonEnabled: Boolean,
    val menuScrollPosition: Int,
    val menuExpandedArtistGroup: String,
    val songListScrollPosition: Int,
    val songListNeedScroll: Boolean
) {
    companion object {
        fun initial(commonState: CommonState) = LocalState(
            commonState = commonState,
            currentSongCount = 0,
            currentSongList = listOf(),
            currentSongPosition = 0,
            currentSong = null,
            isEditorMode = false,
            isAutoPlayMode = false,
            isUploadButtonEnabled = true,
            menuScrollPosition = 0,
            menuExpandedArtistGroup = "",
            songListScrollPosition = 0,
            songListNeedScroll = false
        )
    }

    val currentArtist: String
        get() = commonState.currentArtist

    val artistList: List<String>
        get() = commonState.artistList

    val currentScreenVariant: ScreenVariant
        get() = commonState.currentScreenVariant
}