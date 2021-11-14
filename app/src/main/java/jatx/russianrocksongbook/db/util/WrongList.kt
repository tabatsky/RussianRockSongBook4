package jatx.russianrocksongbook.db

import jatx.russianrocksongbook.data.SongRepository
import jatx.russianrocksongbook.domain.Song

val wrongSongs = listOf(
    Song(artist = "Король и Шут", title = "Анархист"),
    Song(artist = "Настя", title = "Даром"),
    Song(artist = "Lumen", title = "Сид и Нэнси"),
    Song(artist = "Гражданская Оборона", title = "Пластмассовый мир"),
    Song(artist = "Браво", title = "Этот лагерь самый лучший лагерь на Земле"),
    Song(artist = "Кино", title = "Черная река"),
    Song(artist = "Би-2", title = "А мы не ангелы, парень"),
    Song(artist = "Агата Кристи", title = "А мы не ангелы"),
    Song(artist = "Ночные Снайперы", title = "Милая девочка")
)

val wrongArtists = listOf(
    "Аккорды от пользователей",
    "Люмен"
)

fun deleteWrongSongs(songRepo: SongRepository) {
    wrongSongs.forEach {
        songRepo.deleteWrongSong(it.artist, it.title)
    }
}

fun deleteWrongArtists(songRepo: SongRepository) {
    wrongArtists.forEach {
        songRepo.deleteWrongArtist(it)
    }
}
