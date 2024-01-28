package jatx.russianrocksongbook.database.dbinit

import jatx.russianrocksongbook.domain.models.local.Song

internal val wrongSongs = listOf(
    Song(artist = "Король и Шут", title = "Анархист"),
    Song(artist = "Настя", title = "Даром"),
    Song(artist = "Lumen", title = "Сид и Нэнси"),
    Song(artist = "Гражданская Оборона", title = "Пластмассовый мир"),
    Song(artist = "Браво", title = "Этот лагерь самый лучший лагерь на Земле"),
    Song(artist = "Кино", title = "Черная река"),
    Song(artist = "Би-2", title = "А мы не ангелы, парень"),
    Song(artist = "Агата Кристи", title = "А мы не ангелы"),
    Song(artist = "Ночные Снайперы", title = "Милая девочка"),
    Song(artist = "Александр Башлачёв", title = "Если б я был султан...")
)

internal val wrongArtists = listOf(
    "Аккорды от пользователей",
)

internal val wrongArtistsPatch = mapOf(
    "Люмен" to "Lumen",
    "Черный кофе" to "Чёрный Кофе",
    "Чиж и Ко" to "Чиж & Co",
    "The Matrixx" to "Глеб Самойлоff & The Matrixx"
)
