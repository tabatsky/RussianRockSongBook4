package jatx.russianrocksongbook.domain.models

const val USER_SONG_MD5 = "USER"

data class Song(
    var id: Long? = null,
    var artist: String = "",
    var title: String = "",
    var text: String = "",
    var favorite: Boolean = false,
    var deleted: Boolean = false,
    var outOfTheBox: Boolean = true,
    var origTextMD5: String = ""
) {

    // for correct MutableStateFlow working
    override fun equals(other: Any?): Boolean {
        return super.equals(other) && other is Song && favorite == other.favorite
    }

    override fun hashCode(): Int {
        return super.hashCode() + (if (favorite) 1 else 0)
    }
}
