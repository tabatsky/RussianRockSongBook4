package jatx.russianrocksongbook.db.util

import jatx.russianrocksongbook.data.SongRepository
import jatx.russianrocksongbook.domain.Song

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
        val songEntity = songRepo.getSongByArtistAndTitle(it.artist, it.title)
        songEntity?.apply {
            val song = Song(this)
            val patchedText = song.text.replace(it.orig, it.patch)
            song.text = patchedText
            songRepo.updateSong(song)
        }
    }
}