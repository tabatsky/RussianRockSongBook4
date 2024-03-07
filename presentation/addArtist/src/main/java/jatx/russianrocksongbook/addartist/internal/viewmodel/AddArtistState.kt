package jatx.russianrocksongbook.addartist.internal.viewmodel

import jatx.russianrocksongbook.domain.models.local.Song

data class AddArtistState(
    val showUploadDialogForDir: Boolean,
    val newArtist: String,
    val uploadSongList: List<Song>
) {
    companion object {
        fun initial() = AddArtistState(
            showUploadDialogForDir = false,
            newArtist = "",
            uploadSongList = listOf()
        )
    }
}
