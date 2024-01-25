package jatx.russianrocksongbook.navigation

import androidx.navigation.NavBackStackEntry

sealed interface ScreenVariant {
    val destination: String

    object Start: ScreenVariant {
        override val destination: String
            get() = destinationStart
    }

    data class SongList(
        val artist: String,
        val isBackFromSong: Boolean = false
    ): ScreenVariant {
        override val destination: String
            get() = "$destinationSongList/$artist/$isBackFromSong"
    }

    data class Favorite(
        val isBackFromSong: Boolean = false
    ): ScreenVariant {
        override val destination: String
            get() = "$destinationFavorite/$isBackFromSong"
    }

    data class SongText(
        val artist: String,
        val position: Int
        ): ScreenVariant {
        override val destination: String
            get() = "$destinationSongText/$artist/$position"
    }

    data class SongTextByArtistAndTitle(
        val artist: String,
        val title: String
        ): ScreenVariant {
        override val destination: String
            get() = "$destinationSongTextByArtistAndTitle/$artist/$title"
    }

    data class CloudSearch(
        val isBackFromSong: Boolean = false
    ): ScreenVariant {
        override val destination: String
            get() = "$destinationCloudSearch/$isBackFromSong"
    }

    data class CloudSongText(
        val position: Int
    ): ScreenVariant {
        override val destination: String
            get() = "$destinationCloudSongText/$position"
    }

    object AddArtist: ScreenVariant {
        override val destination: String
            get() = destinationAddArtist
    }

    object AddSong: ScreenVariant {
        override val destination: String
            get() = destinationAddSong
    }

    object Donation: ScreenVariant {
        override val destination: String
            get() = destinationDonation
    }

    object Settings: ScreenVariant {
        override val destination: String
            get() = destinationSettings
    }
}

val String.isStartScreen: Boolean
    get() = this.startsWith(destinationStart)

val NavBackStackEntry?.isStartScreen: Boolean
    get() = this?.destination?.route?.isStartScreen ?: false

val String.isSongListScreen: Boolean
    get() = this.startsWith(destinationSongList)

val NavBackStackEntry?.isSongListScreen: Boolean
    get() = this?.destination?.route?.isSongListScreen ?: false

val String.isSongTextScreen: Boolean
    get() = this.startsWith(destinationSongText)

val NavBackStackEntry?.isSongTextScreen: Boolean
    get() = this?.destination?.route?.isSongTextScreen ?: false