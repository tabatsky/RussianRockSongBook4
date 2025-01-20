package jatx.clickablewordstextcompose

import jatx.clickablewordstextcompose.internal.Word
import jatx.clickablewordstextcompose.internal.WordScanner
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun wordScanner_isWorkingCorrect() {
        val text = """
            Проигрыш: F#m C#m 

             F#m
            Теплое место, но улицы ждут
                  C#m
            Отпечатков наших ног.
            F#m                  E
               Звездная пыль - на сапогах.
            F#m
            Мягкое кресло, клетчатый плед,
                  C#m
            Не нажатый вовремя курок.
            F#m                          E
            Солнечный день - в ослепительных снах.

            Припев:
                    F#m
            Группа крови - на рукаве,
                                 C#m
            Мой порядковый номер - на рукаве,
                 Bm                                 E
            Пожелай мне удачи в бою, пожелай мне:
                    F#m
            Не остаться в этой траве,
                   C#m
            Не остаться в этой траве. 
                 Bm                         E            F#m
            Пожелай мне удачи, пожелай мне удачи!
            проигр.  F#m C#m

             F#m
            И есть чем платить, но я не хочу
                  C#m
            Победы любой ценой.
            F#m             E
            Я никому не хочу ставить ногу на грудь.
            F#m
            Я хотел бы остаться с тобой,
                  C#m
            Просто остаться с тобой,
                  F#m                          E
            Но высокая в небе звезда зовет меня в путь.

            Припев:
                    F#m
            Группа крови - на рукаве,
                                 C#m
            Мой порядковый номер - на рукаве,
                 Bm                                 E
            Пожелай мне удачи в бою, пожелай мне:
                    F#m
            Не остаться в этой траве,
                   C#m
            Не остаться в этой траве.
                 Bm                         E
            Пожелай мне удачи, пожелай мне удачи!
            F#m C#m F#m C#m
        """.trimIndent()

        val wordScanner = WordScanner(text)
        val words = wordScanner.getWordList()

        val expectedWords = listOf(
            Word(text="F#m", startIndex=10, endIndex=13),
            Word(text="C#m", startIndex=14, endIndex=17),
            Word(text="F#m", startIndex=21, endIndex=24),
            Word(text="C#m", startIndex=59, endIndex=62),
            Word(text="F#m", startIndex=85, endIndex=88),
            Word(text="E", startIndex=106, endIndex=107),
            Word(text="F#m", startIndex=139, endIndex=142),
            Word(text="C#m", startIndex=180, endIndex=183),
            Word(text="F#m", startIndex=210, endIndex=213),
            Word(text="E", startIndex=239, endIndex=240),
            Word(text="F#m", startIndex=297, endIndex=300),
            Word(text="C#m", startIndex=348, endIndex=351),
            Word(text="Bm", startIndex=391, endIndex=393),
            Word(text="E", startIndex=426, endIndex=427),
            Word(text="F#m", startIndex=474, endIndex=477),
            Word(text="C#m", startIndex=511, endIndex=514),
            Word(text="Bm", startIndex=547, endIndex=549),
            Word(text="E", startIndex=574, endIndex=575),
            Word(text="F#m", startIndex=587, endIndex=590),
            Word(text="F#m", startIndex=638, endIndex=641),
            Word(text="C#m", startIndex=642, endIndex=645),
            Word(text="F#m", startIndex=648, endIndex=651),
            Word(text="C#m", startIndex=691, endIndex=694),
            Word(text="F#m", startIndex=715, endIndex=718),
            Word(text="E", startIndex=731, endIndex=732),
            Word(text="F#m", startIndex=773, endIndex=776),
            Word(text="C#m", startIndex=812, endIndex=815),
            Word(text="F#m", startIndex=847, endIndex=850),
            Word(text="E", startIndex=876, endIndex=877),
            Word(text="F#m", startIndex=939, endIndex=942),
            Word(text="C#m", startIndex=990, endIndex=993),
            Word(text="Bm", startIndex=1033, endIndex=1035),
            Word(text="E", startIndex=1068, endIndex=1069),
            Word(text="F#m", startIndex=1116, endIndex=1119),
            Word(text="C#m", startIndex=1153, endIndex=1156),
            Word(text="Bm", startIndex=1188, endIndex=1190),
            Word(text="E", startIndex=1215, endIndex=1216),
            Word(text="F#m", startIndex=1255, endIndex=1258),
            Word(text="C#m", startIndex=1259, endIndex=1262),
            Word(text="F#m", startIndex=1263, endIndex=1266),
            Word(text="C#m", startIndex=1267, endIndex=1270)
        )

        assert(words.size == expectedWords.size)
        assert(words == expectedWords)
    }
}