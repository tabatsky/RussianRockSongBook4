package jatx.russianrocksongbook.navigation

import androidx.navigation.NavDestination
import kotlin.reflect.KClass

fun String.replaceLast(oldValue: String, newValue: String): String =
    substringBeforeLast(oldValue) + newValue + substringAfterLast(oldValue)

fun NavDestination?.routeClass(): KClass<*>? {
    return this?.route
        ?.split("/", "?")
        ?.first()
        ?.replaceLast(".", "$")
        ?.let { Class.forName(it) }
        ?.kotlin
}

val NavDestination?.isStartScreen: Boolean
    get() = this?.routeClass() == ScreenVariant.Start::class

val NavDestination?.isSongListScreen: Boolean
    get() = this?.routeClass() == ScreenVariant.SongList::class

val NavDestination?.isFavorite: Boolean
    get() = this?.routeClass() == ScreenVariant.Favorite::class

val NavDestination?.isSongTextScreen: Boolean
    get() = this?.routeClass() == ScreenVariant.SongText::class

val NavDestination?.isSongTextByArtistAndTitleScreen: Boolean
    get() = this?.routeClass() == ScreenVariant.SongTextByArtistAndTitle::class

val NavDestination?.isCloudSearchScreen: Boolean
    get() = this?.routeClass() == ScreenVariant.CloudSearch::class

val NavDestination?.isTextSearchListScreen: Boolean
    get() = this?.routeClass() == ScreenVariant.TextSearchList::class

val NavDestination?.isCloudSongTextScreen: Boolean
    get() = this?.routeClass() == ScreenVariant.CloudSongText::class

val NavDestination?.isTextSearchSongTextScreen: Boolean
    get() = this?.routeClass() == ScreenVariant.TextSearchSongText::class

val NavDestination?.isAddArtistScreen: Boolean
    get() = this?.routeClass() == ScreenVariant.AddArtist::class

val NavDestination?.isAddSongScreen: Boolean
    get() = this?.routeClass() == ScreenVariant.AddSong::class

val NavDestination?.isDonationScreen: Boolean
    get() = this?.routeClass() == ScreenVariant.Donation::class

val NavDestination?.isSettingsScreen: Boolean
    get() = this?.routeClass() == ScreenVariant.Settings::class