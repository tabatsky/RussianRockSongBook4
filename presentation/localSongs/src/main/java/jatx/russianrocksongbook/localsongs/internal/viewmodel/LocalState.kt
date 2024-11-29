package jatx.russianrocksongbook.localsongs.internal.viewmodel

import jatx.russianrocksongbook.domain.models.local.Song

data class LocalState(
    val currentSongList: List<Song>,
    val menuScrollPosition: Int,
    val menuExpandedArtistGroup: String
) {

    companion object {
        fun initial() = LocalState(
            currentSongList = listOf(),
            menuScrollPosition = 0,
            menuExpandedArtistGroup = ""
        )
    }
}