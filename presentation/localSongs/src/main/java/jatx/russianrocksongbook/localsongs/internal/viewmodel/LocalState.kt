package jatx.russianrocksongbook.localsongs.internal.viewmodel

import jatx.russianrocksongbook.commonviewmodel.CustomState
import jatx.russianrocksongbook.domain.models.local.Song

data class LocalState(
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
): CustomState {

    override fun toString() = "$currentSongCount ${currentSongList.size} $currentSongPosition ${currentSong?.title}"

    companion object {
        fun initial() = LocalState(
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
}