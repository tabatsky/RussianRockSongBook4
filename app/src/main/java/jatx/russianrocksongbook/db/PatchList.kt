package jatx.russianrocksongbook.db

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
    )
)

fun applySongPatches(songRepo: SongRepository) {
    patches.forEach {
        val song = songRepo.getSongByArtistAndTitle(it.artist, it.title)
        song?.apply {
            val song = Song(this)
            val patchedText = song.text.replace(it.orig, it.patch)
            song.text = patchedText
            songRepo.updateSong(song)
        }
    }
}