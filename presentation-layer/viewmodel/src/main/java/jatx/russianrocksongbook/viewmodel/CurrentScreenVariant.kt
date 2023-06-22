package jatx.russianrocksongbook.viewmodel

sealed class CurrentScreenVariant {
    object START: CurrentScreenVariant()
    data class SONG_LIST(
        val artist: String,
        val isBackFromSong: Boolean = false
    ): CurrentScreenVariant()
    data class FAVORITE(
        val isBackFromSong: Boolean = false
    ): CurrentScreenVariant()
    data class SONG_TEXT(
        val artist: String,
        val position: Int
        ): CurrentScreenVariant()
    data class SONG_TEXT_BY_ARTIST_AND_TITLE(
        val artist: String,
        val title: String
        ): CurrentScreenVariant()
    data class CLOUD_SEARCH(
        val isBackFromSong: Boolean = false
    ): CurrentScreenVariant()
    data class CLOUD_SONG_TEXT(
        val position: Int
    ): CurrentScreenVariant()
    object ADD_ARTIST: CurrentScreenVariant()
    object ADD_SONG: CurrentScreenVariant()
    object DONATION: CurrentScreenVariant()
    object SETTINGS: CurrentScreenVariant()
}