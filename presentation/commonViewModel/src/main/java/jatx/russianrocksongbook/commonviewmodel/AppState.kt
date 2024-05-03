package jatx.russianrocksongbook.commonviewmodel

import jatx.russianrocksongbook.navigation.ScreenVariant

data class AppState(
    val currentScreenVariant: ScreenVariant,
    val previousScreenVariant: ScreenVariant? = null,
    val currentArtist: String,
    val appWasUpdated: Boolean,
    val artistList: List<String>,
    val lastRandomKey: Int
) {

    companion object {
        fun initial(defaultArtist: String) = AppState(
            currentScreenVariant = ScreenVariant.Start,
            currentArtist = defaultArtist,
            appWasUpdated = false,
            artistList = listOf(),
            lastRandomKey = 0
        )
    }
}
