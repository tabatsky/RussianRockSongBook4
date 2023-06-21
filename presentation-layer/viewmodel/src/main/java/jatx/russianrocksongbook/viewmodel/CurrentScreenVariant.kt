package jatx.russianrocksongbook.viewmodel

sealed class CurrentScreenVariant {
    object START: CurrentScreenVariant()
    data class SONG_LIST(
        val artist: String,
        val isBackFromSong: Boolean = false,
        val onSuccess: (() -> Unit)? = null
    ): CurrentScreenVariant()
    data class FAVORITE(val isBackFromSong: Boolean = false): CurrentScreenVariant()
    object SONG_TEXT: CurrentScreenVariant()
    object ADD_ARTIST: CurrentScreenVariant()
    object ADD_SONG: CurrentScreenVariant()
    data class CLOUD_SEARCH(val isBackFromSong: Boolean = false): CurrentScreenVariant()
    object CLOUD_SONG_TEXT: CurrentScreenVariant()
    object DONATION: CurrentScreenVariant()
    object SETTINGS: CurrentScreenVariant()
}