package jatx.russianrocksongbook.gson

import jatx.russianrocksongbook.data.UserInfo
import jatx.russianrocksongbook.db.entities.Song
import jatx.russianrocksongbook.db.entities.USER_SONG_MD5
import jatx.russianrocksongbook.db.entities.songTextHash
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
    val raiting: Double = 0.0
) {
    constructor(song: Song, userInfo: UserInfo): this(
        googleAccount = userInfo.googleAccount,
        deviceIdHash = userInfo.deviceIdHash,
        artist = song.artist,
        title = song.title,
        text = song.text,
        textHash = songTextHash(song.text),
        isUserSong = song.origTextMD5 == USER_SONG_MD5
    )

    val visibleTitle: String
        get() = "$title${if (variant == 0) "" else " ($variant)"}"

    val formattedRating: String
        get() = formatRating(raiting)

    val visibleTitleWithRating: String
        get() = "$visibleTitle | $formattedRating"
}

fun formatRating(rating: Double): String {
    val formatter: NumberFormat = DecimalFormat("###0.000")
    return formatter.format(rating)
}
