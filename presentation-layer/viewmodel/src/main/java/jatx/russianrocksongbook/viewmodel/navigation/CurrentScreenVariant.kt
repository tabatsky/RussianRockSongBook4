package jatx.russianrocksongbook.viewmodel.navigation

sealed interface CurrentScreenVariant {
    val destination: String

    object START: CurrentScreenVariant {
        override val destination: String
            get() = destinationStart
    }

    data class SONG_LIST(
        val artist: String,
        val isBackFromSong: Boolean = false
    ): CurrentScreenVariant {
        override val destination: String
            get() = "$destinationSongList/$artist/$isBackFromSong"
    }

    data class FAVORITE(
        val isBackFromSong: Boolean = false
    ): CurrentScreenVariant {
        override val destination: String
            get() = "$destinationFavorite/$isBackFromSong"
    }

    data class SONG_TEXT(
        val artist: String,
        val position: Int
        ): CurrentScreenVariant {
        override val destination: String
            get() = "$destinationSongText/$artist/$position"
    }

    data class SONG_TEXT_BY_ARTIST_AND_TITLE(
        val artist: String,
        val title: String
        ): CurrentScreenVariant {
        override val destination: String
            get() = "$destinationSongTextByArtistAndTitle/$artist/$title"
    }

    data class CLOUD_SEARCH(
        val isBackFromSong: Boolean = false
    ): CurrentScreenVariant {
        override val destination: String
            get() = "$destinationCloudSearch/$isBackFromSong"
    }

    data class CLOUD_SONG_TEXT(
        val position: Int
    ): CurrentScreenVariant {
        override val destination: String
            get() = "$destinationCloudSongText/$position"
    }

    object ADD_ARTIST: CurrentScreenVariant {
        override val destination: String
            get() = destinationAddArtist
    }

    object ADD_SONG: CurrentScreenVariant {
        override val destination: String
            get() = destinationAddSong
    }

    object DONATION: CurrentScreenVariant {
        override val destination: String
            get() = destinationDonation
    }

    object SETTINGS: CurrentScreenVariant {
        override val destination: String
            get() = destinationSettings
    }
}