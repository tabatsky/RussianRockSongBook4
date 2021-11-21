package jatx.russianrocksongbook.db.util

import android.content.Context
import com.google.gson.Gson
import jatx.russianrocksongbook.R
import jatx.russianrocksongbook.data.SongRepository
import jatx.russianrocksongbook.api.gson.SongBookGson
import jatx.russianrocksongbook.domain.Song
import java.util.*

fun fillDbFromJSON(songRepo: SongRepository, context: Context, onProgressChanged: (Int, Int) -> Unit) {
    val jsonLoader = JsonLoader(context)
    while (jsonLoader.hasNext()) {
        onProgressChanged(jsonLoader.current + 1, jsonLoader.total)
        val songs = jsonLoader.loadNext()
        songRepo.insertIgnoreSongs(songs)
    }
}

class JsonLoader(
    private val context: Context
) {
    var current = 0
    val total: Int
        get() = artists.size

    fun hasNext() = current < artists.size

    fun loadNext(): List<Song> {
        try {
            val artist = artists[current]
            val dict = artistMap[artist]

            current++

            dict?.apply {
                val sc = Scanner(context.resources.openRawResource(this))
                val jsonStr = sc.useDelimiter("\\A").next()
                sc.close()

                val songbook = Gson().fromJson(jsonStr, SongBookGson::class.java)

                return songbook.songbook.map { Song(artist, it) }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        return listOf()
    }
}

val artistMap = mapOf(
    "7Б" to R.raw.b7,
    "Brainstorm" to R.raw.brainstorm,
    "Flёur" to R.raw.flyour,
    "Louna" to R.raw.louna,
    "Lumen" to R.raw.lumen,
    "The Matrixx" to R.raw.samoiloff,
    "Znaki" to R.raw.znaki,
    "Агата Кристи" to R.raw.agata,
    "Аквариум" to R.raw.akvarium,
    "Алиса" to R.raw.alisa,
    "Ария" to R.raw.aria,
    "АукцЫон" to R.raw.auktsyon,
    "Александр Башлачёв" to R.raw.bashlachev,
    "Белая Гвардия" to R.raw.b_gvardia,
    "Би-2" to R.raw.bi2,
    "Браво" to R.raw.bravo,
    "Бригада С" to R.raw.brigada_c,
    "Бригадный Подряд" to R.raw.brigadnyi,
    "Ва-Банкъ" to R.raw.vabank,
    "Високосный год" to R.raw.visokosniy,
    "Воскресенье" to R.raw.voskresenie,
    "Год Змеи" to R.raw.god_zmei,
    "Гражданская Оборона" to R.raw.grob,
    "ДДТ" to R.raw.ddt,
    "Дельфин" to R.raw.dolphin,
    "Земляне" to R.raw.zemlane,
    "Земфира" to R.raw.zemfira,
    "Зоопарк" to R.raw.zoopark,
    "Игорь Тальков" to R.raw.talkov,
    "Калинов Мост" to R.raw.kalinovmost,
    "Кино" to R.raw.kino,
    "КняZz" to R.raw.knazz,
    "Коридор" to R.raw.koridor,
    "Король и Шут" to R.raw.kish,
    "Крематорий" to R.raw.krematoriy,
    "Кукрыниксы" to R.raw.kukryniksy,
    "Ленинград" to R.raw.leningrad,
    "Линда" to R.raw.linda,
    "Любэ" to R.raw.lyube,
    "Маша и Медведи" to R.raw.mashamedv,
    "Машина Времени" to R.raw.machina,
    "Мельница" to R.raw.melnitsa,
    "Мультfильмы" to R.raw.multfilmi,
    "Мумий Тролль" to R.raw.mumiytrol,
    "Наив" to R.raw.naiv,
    "Настя" to R.raw.nastia,
    "Наутилус Помпилиус" to R.raw.nautilus,
    "Неприкасаемые" to R.raw.neprikasaemye,
    "Немного Нервно" to R.raw.nervno,
    "Ногу Свело" to R.raw.nogusvelo,
    "Ноль" to R.raw.nol,
    "Ночные Снайперы" to R.raw.snaipery,
    "Павел Кашин" to R.raw.kashin,
    "Пикник" to R.raw.piknik,
    "Пилот" to R.raw.pilot,
    "Порнофильмы" to R.raw.pornofilmy,
    "Сектор Газа" to R.raw.sektor,
    "СерьГа" to R.raw.serga,
    "Смысловые Галлюцинации" to R.raw.smislovie,
    "Сплин" to R.raw.splin,
    "Танцы Минус" to R.raw.minus,
    "Торба-на-Круче" to R.raw.torba_n,
    "Чайф" to R.raw.chaif,
    "Черный кофе" to R.raw.cherniykofe,
    "Чичерина" to R.raw.chicherina,
    "Чиж и Ко" to R.raw.chizh,
    "Эпидемия" to R.raw.epidemia,
    "Юта" to R.raw.uta,
    "Янка Дягилева" to R.raw.yanka
)

val artists = artistMap.keys.toTypedArray()