package jatx.russianrocksongbook.db

import jatx.russianrocksongbook.data.SongRepository

data class SongPatch(
    val artist: String,
    val title: String,
    val orig: String,
    val patch: String
)

val patches = listOf(
    SongPatch(
        "Гражданская Оборона",
        "Все идет по плану",
        "А наш батюшка Ленин совсем усох",
        "А наш батюшка Ленин совсем усоп"
    )
)

fun applySongPatches(songRepo: SongRepository) {
    patches.forEach {
        val song = songRepo.getSongByArtistAndTitle(it.artist, it.title)
        song?.apply {
            val patchedText = this.text.replace(it.orig, it.patch)
            songRepo.updateSong(this.withText(patchedText))
        }
    }
}