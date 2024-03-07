package jatx.russianrocksongbook.addsong.internal.viewmodel

import jatx.russianrocksongbook.domain.models.local.Song

data class AddSongState(
    val showUploadDialogForSong: Boolean,
    val newSong: Song?
) {
    companion object {
        fun initial() = AddSongState(
            showUploadDialogForSong = false,
            newSong = null
        )
    }
}
