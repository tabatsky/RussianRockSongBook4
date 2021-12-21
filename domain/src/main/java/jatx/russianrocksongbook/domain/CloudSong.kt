package jatx.russianrocksongbook.domain

import java.text.DecimalFormat
import java.text.NumberFormat

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

//    val formattedRating: String
//        get() = formatRating(raiting)

    val thumbUp = "\uD83D\uDC4D"
    val thumbDown = "\uD83D\uDC4E"

    val formattedRating: String
        get() = "$thumbUp$likeCount $thumbDown$dislikeCount"

    val visibleTitleWithRating: String
        get() = "$visibleTitle | $formattedRating"

    val visibleTitleWithArtistAndRating: String
        get() = "$visibleTitle | $artist | $formattedRating"
}

fun formatRating(rating: Double): String {
    val formatter: NumberFormat = DecimalFormat("###0.000")
    return formatter.format(rating)
}
