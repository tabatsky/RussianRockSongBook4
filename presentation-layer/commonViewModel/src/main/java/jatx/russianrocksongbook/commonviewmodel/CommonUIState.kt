package jatx.russianrocksongbook.commonviewmodel

import jatx.russianrocksongbook.navigation.ScreenVariant

interface UIState

data class CommonUIState(
    val currentScreenVariant: ScreenVariant,
    val currentArtist: String,
    val appWasUpdated: Boolean,
    val artistList: List<String>
): UIState {
    companion object {
        fun initial(defaultArtist: String) = CommonUIState(
            currentScreenVariant = ScreenVariant.Start,
            currentArtist = defaultArtist,
            appWasUpdated = false,
            artistList = listOf()
        )
    }
}