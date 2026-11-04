package jatx.russianrocksongbook.addartist.internal.viewmodel

import jatx.russianrocksongbook.commonappstate.UIStateKey
import jatx.russianrocksongbook.commonappstate.UIState
import jatx.russianrocksongbook.domain.models.local.Song

data class AddArtistState(
    val showUploadDialogForDir: Boolean,
    val newArtist: String,
    val uploadSongList: List<Song>
) : UIState {
    companion object {
        fun initial() = AddArtistState(
            showUploadDialogForDir = false,
            newArtist = "",
            uploadSongList = listOf()
        )
    }
}

internal val AddArtistStateKey = UIStateKey(AddArtistState::class.java) { AddArtistState.initial() }
