package jatx.russianrocksongbook.database.internal.db.util

internal data class SongPatch(
    val artist: String,
    val title: String,
    val orig: String,
    val patch: String
)

internal val patches = listOf(
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
