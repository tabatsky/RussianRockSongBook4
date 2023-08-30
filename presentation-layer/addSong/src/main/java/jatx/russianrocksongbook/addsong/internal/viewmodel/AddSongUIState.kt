package jatx.russianrocksongbook.addsong.internal.viewmodel

import jatx.russianrocksongbook.commonviewmodel.UIState
import jatx.russianrocksongbook.domain.models.local.Song

data class AddSongUIState(
    val showUploadDialogForSong: Boolean,
    val newSong: Song?
): UIState {
    companion object {
        fun initial() = AddSongUIState(
            showUploadDialogForSong = false,
            newSong = null
        )
    }
}
