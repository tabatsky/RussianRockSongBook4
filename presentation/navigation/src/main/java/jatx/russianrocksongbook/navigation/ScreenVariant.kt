package jatx.russianrocksongbook.navigation

import androidx.navigation.NavDestination
import kotlin.random.Random

val rnd = Random(42)

sealed interface ScreenVariant {
    val destination: String

    data object Start: ScreenVariant {
        override val destination: String
            get() = destinationStart
    }

    data class SongList(
        val artist: String,
        val isBackFromSomeScreen: Boolean = false
    ): ScreenVariant {
        override val destination: String
            get() = "$destinationSongList/$artist/$isBackFromSomeScreen"
    }

    data class Favorite(
        val isBackFromSomeScreen: Boolean = false
    ): ScreenVariant {
        override val destination: String
            get() = "$destinationFavorite/$isBackFromSomeScreen"
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
        val randomKey: Int = rnd.nextInt(),
        val isBackFromSong: Boolean = false
    ): ScreenVariant {
        override val destination: String
            get() = "$destinationCloudSearch/$randomKey/$isBackFromSong"
    }

    data class CloudSongText(
        val position: Int
    ): ScreenVariant {
        override val destination: String
            get() = "$destinationCloudSongText/$position"
    }

    data object AddArtist: ScreenVariant {
        override val destination: String
            get() = destinationAddArtist
    }

    data object AddSong: ScreenVariant {
        override val destination: String
            get() = destinationAddSong
    }

    data object Donation: ScreenVariant {
        override val destination: String
            get() = destinationDonation
    }

    data object Settings: ScreenVariant {
        override val destination: String
            get() = destinationSettings
    }
}

val String.isStartScreen: Boolean
    get() = this.startsWith(destinationStart)

val NavDestination?.isStartScreen: Boolean
    get() = this?.route?.isStartScreen ?: false

val String.isSongListScreen: Boolean
    get() = this.startsWith(destinationSongList)

val NavDestination?.isSongListScreen: Boolean
    get() = this?.route?.isSongListScreen ?: false

val String.isFavorite: Boolean
    get() = this.startsWith(destinationFavorite)

val NavDestination?.isFavorite: Boolean
    get() = this?.route?.isFavorite ?: false

val String.isSongTextScreen: Boolean
    get() = this.startsWith(destinationSongText) &&
            !this.startsWith(destinationSongTextByArtistAndTitle)

val NavDestination?.isSongTextScreen: Boolean
    get() = this?.route?.isSongTextScreen ?: false

val String.isSongTextByArtistAndTitleScreen: Boolean
    get() = this.startsWith(destinationSongTextByArtistAndTitle)

val NavDestination?.isSongTextByArtistAndTitleScreen: Boolean
    get() = this?.route?.isSongTextByArtistAndTitleScreen ?: false

val String.isCloudSearchScreen: Boolean
    get() = this.startsWith(destinationCloudSearch)

val NavDestination?.isCloudSearchScreen: Boolean
    get() = this?.route?.isCloudSearchScreen ?: false

val String.isCloudSongTextScreen: Boolean
    get() = this.startsWith(destinationCloudSongText)

val NavDestination?.isCloudSongTextScreen: Boolean
    get() = this?.route?.isCloudSongTextScreen ?: false

val String.isAddArtistScreen: Boolean
    get() = this.startsWith(destinationAddArtist)

val NavDestination?.isAddArtistScreen: Boolean
    get() = this?.route?.isAddArtistScreen ?: false

val String.isAddSongScreen: Boolean
    get() = this.startsWith(destinationAddSong)

val NavDestination?.isAddSongScreen: Boolean
    get() = this?.route?.isAddSongScreen ?: false

val String.isDonationScreen: Boolean
    get() = this.startsWith(destinationDonation)

val NavDestination?.isDonationScreen: Boolean
    get() = this?.route?.isDonationScreen ?: false

val String.isSettingsScreen: Boolean
    get() = this.startsWith(destinationSettings)

val NavDestination?.isSettingsScreen: Boolean
    get() = this?.route?.isSettingsScreen ?: false