package jatx.russianrocksongbook.commonsongtext.viewmodel

import jatx.russianrocksongbook.domain.models.local.Song

data class CommonSongTextState(
    val currentSongCount: Int,
    val currentSongPosition: Int,
    val currentSong: Song?,
    val isEditorMode: Boolean,
    val isAutoPlayMode: Boolean,
    val isUploadButtonEnabled: Boolean,
    val songListScrollPosition: Int,
    val songListNeedScroll: Boolean
) {

    companion object {
        fun initial() = CommonSongTextState(
            currentSongCount = 0,
            currentSongPosition = 0,
            currentSong = null,
            isEditorMode = false,
            isAutoPlayMode = false,
            isUploadButtonEnabled = true,
            songListScrollPosition = 0,
            songListNeedScroll = false
        )
    }
}