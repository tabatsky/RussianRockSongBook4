package jatx.russianrocksongbook.commonviewmodel

import jatx.russianrocksongbook.navigation.*

data class AppState(
    val currentScreenVariant: ScreenVariant,
    val currentArtist: String,
    val appWasUpdated: Boolean,
    val artistList: List<String>,
    val lastRandomKey: Int
) {

    companion object {
        fun initial(defaultArtist: String) = AppState(
            currentScreenVariant = StartScreenVariant,
            currentArtist = defaultArtist,
            appWasUpdated = false,
            artistList = listOf(),
            lastRandomKey = 0
        )
    }
}
