package jatx.russianrocksongbook.navigation

import kotlin.random.Random

val rnd = Random(42)

sealed interface ScreenVariant {
    val route: Any

    data object Start: ScreenVariant {
        override val route: Any
            get() = StartRoute
    }

    data class SongList(
        val artist: String,
        val isBackFromSomeScreen: Boolean = false
    ): ScreenVariant {
        override val route: Any
            get() = SongListRoute(artist, isBackFromSomeScreen)
    }

    data class Favorite(
        val isBackFromSomeScreen: Boolean = false
    ): ScreenVariant {
        override val route: Any
            get() = FavoriteRoute(isBackFromSomeScreen)
    }

    data class SongText(
        val artist: String,
        val position: Int
        ): ScreenVariant {
        override val route: Any
            get() = SongTextRoute(artist, position)
    }

    data class SongTextByArtistAndTitle(
        val artist: String,
        val title: String
        ): ScreenVariant {
        override val route: Any
            get() = SongTextByArtistAndTitleRoute(artist, title)
    }

    data class CloudSearch(
        val randomKey: Int = rnd.nextInt(),
        val isBackFromSong: Boolean = false
    ): ScreenVariant {
        override val route: Any
            get() = CloudSearchRoute(randomKey, isBackFromSong)
    }

    data class CloudSongText(
        val position: Int
    ): ScreenVariant {
        override val route: Any
            get() = CloudSongTextRoute(position)
    }

    data class TextSearchList(
        val randomKey: Int = rnd.nextInt(),
        val isBackFromSong: Boolean = false
    ): ScreenVariant {
        override val route: Any
            get() = TextSearchListRoute(randomKey, isBackFromSong)
    }

    data class TextSearchSongText(
        val position: Int
    ): ScreenVariant {
        override val route: Any
            get() = TextSearchSongTextRoute(position)
    }

    data object AddArtist: ScreenVariant {
        override val route: Any
            get() = AddArtistRoute
    }

    data object AddSong: ScreenVariant {
        override val route: Any
            get() = AddSongRoute
    }

    data object Donation: ScreenVariant {
        override val route: Any
            get() = DonationRoute
    }

    data object Settings: ScreenVariant {
        override val route: Any
            get() = SettingsRoute
    }
}
