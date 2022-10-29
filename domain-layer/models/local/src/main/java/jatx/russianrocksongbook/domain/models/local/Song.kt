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
        return other is Song &&
                id == other.id &&
                artist == other.artist &&
                title == other.title &&
                text == other.text &&
                origTextMD5 == other.origTextMD5 &&
                outOfTheBox == other.outOfTheBox &&
                deleted == other.deleted &&
                favorite == other.favorite
    }

    override fun hashCode(): Int {
        return (id?.toInt() ?: 0) * 128 +
                artist.hashCode() * 64 +
                title.hashCode() * 32 +
                text.hashCode() * 16 +
                origTextMD5.hashCode() * 8 +
                (if (outOfTheBox) 1 else 0) * 4 +
                (if (deleted) 1 else 0) * 2 +
                (if (favorite) 1 else 0)
    }
}
