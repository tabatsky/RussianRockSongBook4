package jatx.russianrocksongbook.navigation

import androidx.navigation.NavDestination

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

val String.isTextSearchListScreen: Boolean
    get() = this.startsWith(destinationTextSearchList)

val NavDestination?.isTextSearchListScreen: Boolean
    get() = this?.route?.isTextSearchListScreen ?: false

val String.isTextSearchSongTextScreen: Boolean
    get() = this.startsWith(destinationTextSearchSongText)

val NavDestination?.isTextSearchSongTextScreen: Boolean
    get() = this?.route?.isTextSearchSongTextScreen ?: false

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