package jatx.russianrocksongbook.navigation

import androidx.navigation.NavDestination
import kotlin.reflect.KClass

fun NavDestination?.routeClass(): KClass<*>? {
    return this?.route
        ?.split("/")
        ?.first()
        ?.let { Class.forName(it) }
        ?.kotlin
}

val NavDestination?.isStartScreen: Boolean
    get() = this?.routeClass() == StartRoute::class

val NavDestination?.isSongListScreen: Boolean
    get() = this?.routeClass() == SongListRoute::class

val NavDestination?.isFavorite: Boolean
    get() = this?.routeClass() == FavoriteRoute::class

val NavDestination?.isSongTextScreen: Boolean
    get() = this?.routeClass() == SongTextRoute::class

val NavDestination?.isSongTextByArtistAndTitleScreen: Boolean
    get() = this?.routeClass() == SongTextByArtistAndTitleRoute::class

val NavDestination?.isCloudSearchScreen: Boolean
    get() = this?.routeClass() == CloudSearchRoute::class

val NavDestination?.isTextSearchListScreen: Boolean
    get() = this?.routeClass() == TextSearchListRoute::class

val NavDestination?.isCloudSongTextScreen: Boolean
    get() = this?.routeClass() == CloudSongTextRoute::class

val NavDestination?.isTextSearchSongTextScreen: Boolean
    get() = this?.routeClass() == TextSearchSongTextRoute::class

val NavDestination?.isAddArtistScreen: Boolean
    get() = this?.routeClass() == AddArtistRoute::class

val NavDestination?.isAddSongScreen: Boolean
    get() = this?.routeClass() == AddSongRoute::class

val NavDestination?.isDonationScreen: Boolean
    get() = this?.routeClass() == DonationRoute::class

val NavDestination?.isSettingsScreen: Boolean
    get() = this?.routeClass() == SettingsRoute::class