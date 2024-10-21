package jatx.russianrocksongbook.navigation

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

    data class TextSearchList(
        val randomKey: Int = rnd.nextInt(),
        val isBackFromSong: Boolean = false
    ): ScreenVariant {
        override val destination: String
            get() = "$destinationTextSearchList/$randomKey/$isBackFromSong"
    }

    data class TextSearchSongText(
        val position: Int
    ): ScreenVariant {
        override val destination: String
            get() = "$destinationTextSearchSongText/$position"
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
