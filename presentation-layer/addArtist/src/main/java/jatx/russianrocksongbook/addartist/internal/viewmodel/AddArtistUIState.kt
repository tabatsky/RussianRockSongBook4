package jatx.russianrocksongbook.addartist.internal.viewmodel

import jatx.russianrocksongbook.commonviewmodel.UIState
import jatx.russianrocksongbook.domain.models.local.Song

data class AddArtistUIState(
    val showUploadDialogForDir: Boolean,
    val newArtist: String,
    val uploadSongList: List<Song>
): UIState {
    companion object {
        fun initial() = AddArtistUIState(
            showUploadDialogForDir = false,
            newArtist = "",
            uploadSongList = listOf()
        )
    }
}
