package jatx.russianrocksongbook.localsongs.api.methods

import jatx.russianrocksongbook.localsongs.internal.viewmodel.LocalViewModel
import jatx.russianrocksongbook.navigation.CurrentScreenVariant

fun selectArtist(artist: String) {
    LocalViewModel.getStoredInstance()?.selectArtist(artist)
}

fun selectSongByArtistAndTitle(artist: String, title: String) {
    LocalViewModel.getStoredInstance()?.selectScreen(
        CurrentScreenVariant
            .SONG_TEXT_BY_ARTIST_AND_TITLE(artist, title)
    )
}
