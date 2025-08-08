package jatx.russianrocksongbook.commonappstate

data class AppState(
    val currentArtist: String,
    val appWasUpdated: Boolean,
    val artistList: List<String>,
    val lastRandomKey: Int
) {

    companion object {
        fun initial(defaultArtist: String) = AppState(
            currentArtist = defaultArtist,
            appWasUpdated = false,
            artistList = listOf(),
            lastRandomKey = 0
        )
    }
}