package jatx.russianrocksongbook.commonviewmodel

import jatx.russianrocksongbook.navigation.ScreenVariant

data class CommonState(
    val currentScreenVariant: ScreenVariant,
    val previousScreenVariant: ScreenVariant? = null,
    val currentArtist: String,
    val appWasUpdated: Boolean,
    val artistList: List<String>
) {
    companion object {
        fun initial(defaultArtist: String) = CommonState(
            currentScreenVariant = ScreenVariant.Start,
            currentArtist = defaultArtist,
            appWasUpdated = false,
            artistList = listOf()
        )
    }
}