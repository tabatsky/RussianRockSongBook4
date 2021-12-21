package jatx.russianrocksongbook.domain

const val thumbUp = "\uD83D\uDC4D"
const val thumbDown = "\uD83D\uDC4E"

data class CloudSong(
    val songId: Int = -1,
    val googleAccount: String = "",
    val deviceIdHash: String = "",
    val artist: String = "",
    val title: String = "",
    val text: String = "",
    val textHash: String = "",
    val isUserSong: Boolean = false,
    val variant: Int = -1,
    val raiting: Double = 0.0,
    var likeCount: Int = 0,
    var dislikeCount: Int = 0
) {

    val visibleTitle = "$title${if (variant == 0) "" else " ($variant)"}"

    private val formattedRating: String
        get() = "$thumbUp$likeCount $thumbDown$dislikeCount"

    val visibleTitleWithRating: String
        get() = "$visibleTitle | $formattedRating"

    val visibleTitleWithArtistAndRating: String
        get() = "$visibleTitle | $artist | $formattedRating"
}
