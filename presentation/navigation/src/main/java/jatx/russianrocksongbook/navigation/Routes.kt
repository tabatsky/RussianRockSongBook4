package jatx.russianrocksongbook.navigation

import kotlinx.serialization.Serializable

@Serializable
data object StartRoute

@Serializable
data class SongListRoute(
    val artist: String,
    val isBackFromSomeScreen: Boolean
)

@Serializable
data class FavoriteRoute(
    val isBackFromSomeScreen: Boolean
)

@Serializable
data class SongTextRoute(
    val position: Int,
    val randomKey: Int
)

@Serializable
data class SongTextByArtistAndTitleRoute(
    val artist: String,
    val title: String
)

@Serializable
data class CloudSearchRoute(
    val randomKey: Int,
    val isBackFromSong: Boolean
)

@Serializable
data class TextSearchListRoute(
    val randomKey: Int,
    val isBackFromSong: Boolean
)

@Serializable
data class CloudSongTextRoute(
    val position: Int
)

@Serializable
data class TextSearchSongTextRoute(
    val position: Int,
    val randomKey: Int
)

@Serializable
data object AddArtistRoute

@Serializable
data object AddSongRoute

@Serializable
data object DonationRoute

@Serializable
data object SettingsRoute
