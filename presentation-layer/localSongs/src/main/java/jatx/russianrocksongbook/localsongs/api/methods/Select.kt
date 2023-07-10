package jatx.russianrocksongbook.localsongs.api.methods

import jatx.russianrocksongbook.localsongs.internal.viewmodel.LocalViewModel
import jatx.russianrocksongbook.navigation.ScreenVariant

fun selectArtist(artist: String) {
    LocalViewModel.getStoredInstance()?.selectArtist(artist)
}

fun selectSongByArtistAndTitle(artist: String, title: String) {
    LocalViewModel.getStoredInstance()?.selectScreen(
        ScreenVariant
            .SongTextByArtistAndTitle(artist, title)
    )
}
