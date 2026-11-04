package jatx.russianrocksongbook.addsong.internal.viewmodel

import jatx.russianrocksongbook.commonappstate.UIStateKey
import jatx.russianrocksongbook.commonappstate.UIState
import jatx.russianrocksongbook.domain.models.local.Song

data class AddSongState(
    val showUploadDialogForSong: Boolean,
    val newSong: Song?
) : UIState {
    companion object {
        fun initial() = AddSongState(
            showUploadDialogForSong = false,
            newSong = null
        )
    }
}

internal val AddSongStateKey = UIStateKey(AddSongState::class.java) { AddSongState.initial() }
