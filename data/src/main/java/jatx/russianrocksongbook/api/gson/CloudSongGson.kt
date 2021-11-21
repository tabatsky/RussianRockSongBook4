package jatx.russianrocksongbook.api.gson

data class CloudSongGson(
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
)