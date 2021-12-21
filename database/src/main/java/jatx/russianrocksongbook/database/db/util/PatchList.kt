package jatx.russianrocksongbook.database.db.util

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
    ),
    SongPatch(
        "ДДТ",
        "Styx",
        "кто на спинах плетей(?)",
        "кто на спинах блядей."
    ),
    SongPatch(
        "Гражданская Оборона",
        "Солнцевоpот",
        "Словно сабли свет.",
        "Словно санный след."
    )
)

fun applySongPatches(songRepo: SongRepository) {
    patches.forEach {
        songRepo.getSongByArtistAndTitle(it.artist, it.title)?.apply {
            val patchedText = this.text.replace(it.orig, it.patch)
            this.text = patchedText
            songRepo.updateSong(this)
        }
    }
}