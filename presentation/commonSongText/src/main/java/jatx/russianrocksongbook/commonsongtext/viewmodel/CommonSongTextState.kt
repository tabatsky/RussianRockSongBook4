package jatx.russianrocksongbook.commonsongtext.viewmodel

import jatx.russianrocksongbook.commonappstate.UIStateKey
import jatx.russianrocksongbook.commonappstate.UIState
import jatx.russianrocksongbook.domain.models.local.Song

data class CommonSongTextState(
    val currentSongCount: Int,
    val lastRandomKey: Int,
    val currentSongPosition: Int,
    val currentSong: Song?,
    val isEditorMode: Boolean,
    val isAutoPlayMode: Boolean,
    val isUploadButtonEnabled: Boolean,
    val songListScrollPosition: Int,
    val songListNeedScroll: Boolean,
    val showVkDialog: Boolean,
    val showYandexDialog: Boolean,
    val showYoutubeDialog: Boolean,
    val showUploadDialog: Boolean,
    val showDeleteToTrashDialog: Boolean,
    val showWarningDialog: Boolean,
    val showChordDialog: Boolean,
    val selectedChord: String
) : UIState {

    companion object {
        fun initial() = CommonSongTextState(
            currentSongCount = 0,
            lastRandomKey = 0,
            currentSongPosition = 0,
            currentSong = null,
            isEditorMode = false,
            isAutoPlayMode = false,
            isUploadButtonEnabled = true,
            songListScrollPosition = 0,
            songListNeedScroll = false,
            showVkDialog = false,
            showYandexDialog = false,
            showYoutubeDialog = false,
            showUploadDialog = false,
            showDeleteToTrashDialog = false,
            showWarningDialog = false,
            showChordDialog = false,
            selectedChord = ""
        )
    }
}

/** У каждого экрана-владельца свой независимый CommonSongTextState. */
fun newCommonSongTextStateKey(owner: String) =
    UIStateKey(CommonSongTextState::class.java, "${CommonSongTextState::class.java.name}#$owner") { CommonSongTextState.initial() }
