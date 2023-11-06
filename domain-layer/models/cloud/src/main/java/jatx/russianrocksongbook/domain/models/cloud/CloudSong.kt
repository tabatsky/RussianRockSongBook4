package jatx.russianrocksongbook.domain.models.cloud

import jatx.russianrocksongbook.domain.models.music.Music
import jatx.russianrocksongbook.domain.models.warning.TYPE_CLOUD
import jatx.russianrocksongbook.domain.models.warning.Warnable
import jatx.russianrocksongbook.domain.models.warning.Warning

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
    val likeCount: Int = 0,
    val dislikeCount: Int = 0
): Music, Warnable {

    val visibleTitle = "$title${if (variant == 0) "" else " ($variant)"}"

    private val formattedRating: String
        get() = "$thumbUp$likeCount $thumbDown$dislikeCount"

    val visibleTitleWithRating: String
        get() = "$visibleTitle | $formattedRating"

    val visibleTitleWithArtistAndRating: String
        get() = "$visibleTitle | $artist | $formattedRating"

    override val searchFor: String
        get() = "$artist $title"

    override fun warningWithComment(comment: String) = Warning(
        warningType = TYPE_CLOUD,
        artist = artist,
        title = title,
        variant = variant,
        comment = comment
    )
}
