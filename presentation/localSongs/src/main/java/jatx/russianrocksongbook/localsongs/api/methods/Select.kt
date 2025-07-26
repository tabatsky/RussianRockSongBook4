package jatx.russianrocksongbook.localsongs.api.methods

import jatx.russianrocksongbook.commonviewmodel.SelectScreen
import jatx.russianrocksongbook.localsongs.internal.viewmodel.LocalViewModel
import jatx.russianrocksongbook.localsongs.internal.viewmodel.SelectArtist
import jatx.russianrocksongbook.navigation.*

fun selectArtist(artist: String) {
    LocalViewModel.getStoredInstance()?.submitAction(SelectArtist(artist))
}

fun selectSongByArtistAndTitle(artist: String, title: String) {
    LocalViewModel.getStoredInstance()?.submitAction(
        SelectScreen(
            SongTextByArtistAndTitleScreenVariant(artist, title)
        )
    )
}
