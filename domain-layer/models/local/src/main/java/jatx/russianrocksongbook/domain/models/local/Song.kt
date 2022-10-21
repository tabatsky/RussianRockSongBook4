package jatx.russianrocksongbook.domain.models.local

const val USER_SONG_MD5 = "USER"

data class Song(
    val id: Long? = null,
    val artist: String = "",
    val title: String = "",
    val text: String = "",
    val favorite: Boolean = false,
    val deleted: Boolean = false,
    val outOfTheBox: Boolean = true,
    val origTextMD5: String = ""
) {

    // for correct MutableStateFlow working
    override fun equals(other: Any?): Boolean {
        return super.equals(other) && other is Song && favorite == other.favorite
    }

    override fun hashCode(): Int {
        return super.hashCode() + (if (favorite) 1 else 0)
    }
}
