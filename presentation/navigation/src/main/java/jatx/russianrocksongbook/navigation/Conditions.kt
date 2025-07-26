package jatx.russianrocksongbook.navigation

val ScreenVariant?.isEmptyScreen: Boolean
    get() = this?.let { it::class == EmptyScreenVariant::class } ?: false

val ScreenVariant?.isStartScreen: Boolean
    get() = this?.let { it::class == StartScreenVariant::class } ?: false

val ScreenVariant?.isSongListScreen: Boolean
    get() = this?.let { it::class == SongListScreenVariant::class } ?: false

val ScreenVariant?.isFavoriteScreen: Boolean
    get() = this?.let { it::class == FavoriteScreenVariant::class } ?: false

val ScreenVariant?.isSongTextScreen: Boolean
    get() = this?.let { it::class == SongTextScreenVariant::class } ?: false

val ScreenVariant?.isSongTextByArtistAndTitleScreen: Boolean
    get() = this?.let { it::class == SongTextByArtistAndTitleScreenVariant::class } ?: false

val ScreenVariant?.isCloudSearchScreen: Boolean
    get() = this?.let { it::class == CloudSearchScreenVariant::class } ?: false

val ScreenVariant?.isTextSearchListScreen: Boolean
    get() = this?.let { it::class == TextSearchListScreenVariant::class } ?: false

val ScreenVariant?.isCloudSongTextScreen: Boolean
    get() = this?.let { it::class == CloudSongTextScreenVariant::class } ?: false

val ScreenVariant?.isTextSearchSongTextScreen: Boolean
    get() = this?.let { it::class == TextSearchSongTextScreenVariant::class } ?: false

val ScreenVariant?.isAddArtistScreen: Boolean
    get() = this?.let { it::class == AddArtistScreenVariant::class } ?: false

val ScreenVariant?.isAddSongScreen: Boolean
    get() = this?.let { it::class == AddSongScreenVariant::class } ?: false

val ScreenVariant?.isDonationScreen: Boolean
    get() = this?.let { it::class == DonationScreenVariant::class } ?: false

val ScreenVariant?.isSettingsScreen: Boolean
    get() = this?.let { it::class == SettingsScreenVariant::class } ?: false