package jatx.russianrocksongbook.domain.models.local

import jatx.russianrocksongbook.domain.models.music.Music
import jatx.russianrocksongbook.domain.models.warning.TYPE_OUT_OF_THE_BOX
import jatx.russianrocksongbook.domain.models.warning.Warnable
import jatx.russianrocksongbook.domain.models.warning.Warning

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
): Music, Warnable {

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

    override val searchFor: String
        get() = "$artist $title"

    override fun warningWithComment(comment: String) = Warning(
        warningType = TYPE_OUT_OF_THE_BOX,
        artist = artist,
        title = title,
        variant = -1,
        comment = comment
    )
}
