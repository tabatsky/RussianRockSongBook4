package jatx.russianrocksongbook

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import jatx.russianrocksongbook.domain.repository.*
import jatx.russianrocksongbook.donation.api.view.donationLabel
import jatx.russianrocksongbook.helpers.api.DONATIONS
import jatx.russianrocksongbook.testing.*
import jatx.russianrocksongbook.view.CurrentScreen
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runners.MethodSorters
import javax.inject.Inject


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

const val ARTIST_1 = "Немного Нервно"
const val ARTIST_2 = "Чайф"

const val TITLE_1 = "Santa Maria"
const val TITLE_2 = "Яблочный остров"
const val TITLE_3 = "17 лет"
const val TITLE_4 = "Поплачь о нем"
const val TITLE_5 = "Над мертвым городом сон"

const val ARTIST_NEW = "Новый исполнитель"
const val TITLE_NEW = "Новая песня"
val TEXT_NEW = """
    Какой-то
    Текст песни
    С какими-то
    Аккордами
""".trimIndent()

class HiltTestRunner : AndroidJUnitRunner() {
    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?,
    ): Application {
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}

@ExperimentalTestApi
@HiltAndroidTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class ExampleInstrumentedTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var localRepo: LocalRepository

    @Inject
    lateinit var cloudRepository: CloudRepository

    @Inject
    lateinit var settingsRepository: SettingsRepository

    @Inject
    lateinit var stringConst: StringConst

    @ExperimentalFoundationApi
    @Before
    fun init() {
        TestingConfig.isTesting = true

        hiltRule.inject()

        settingsRepository.orientation = Orientation.RANDOM
        settingsRepository.defaultArtist = ARTIST_KINO

        composeTestRule.activityRule.scenario.recreate()

        composeTestRule.setContent {
            CurrentScreen()
        }
    }

    @Test
    fun test1_menuAndSongList() {
        val testNumber = 1

        val appWasUpdated = settingsRepository.appWasUpdated

        while (settingsRepository.appWasUpdated) {
            composeTestRule.waitFor(1000L)
        }

        val artists = localRepo.getArtistsAsList()

        if (appWasUpdated) {
            composeTestRule
                .onNodeWithText(stringConst.ok)
                .performClick()
            Log.e("test $testNumber click", stringConst.ok)
            composeTestRule.waitFor(timeout)
        }

        composeTestRule
            .onNodeWithTag(DRAWER_BUTTON_MAIN)
            .performClick()
        Log.e("test $testNumber click", DRAWER_BUTTON_MAIN)
        composeTestRule.waitFor(timeout)
        composeTestRule
            .onNodeWithText(ARTIST_FAVORITE)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$ARTIST_FAVORITE is displayed")
        composeTestRule
            .onNodeWithText(ARTIST_ADD_ARTIST)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$ARTIST_ADD_ARTIST is displayed")
        composeTestRule
            .onNodeWithText(ARTIST_ADD_SONG)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$ARTIST_ADD_SONG is displayed")
        composeTestRule
            .onNodeWithText(ARTIST_CLOUD_SONGS)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$ARTIST_CLOUD_SONGS is displayed")
        composeTestRule
            .onNodeWithText(ARTIST_DONATION)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$ARTIST_DONATION is displayed")

        val index1 = artists.indexOf(ARTIST_1) - 3
        composeTestRule
            .onNodeWithText(ARTIST_1)
            .assertDoesNotExist()
        Log.e("test $testNumber assert", "$ARTIST_1 does not exist")
        composeTestRule
            .onNodeWithTag(MENU_LAZY_COLUMN)
            .performScrollToIndex(index1)
        Log.e("test $testNumber scroll", "menu to index $index1")
        composeTestRule.waitFor(timeout)
        composeTestRule
            .onNodeWithText(ARTIST_1)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$ARTIST_1 is displayed")
        composeTestRule
            .onNodeWithText(ARTIST_1)
            .performClick()
        composeTestRule.waitFor(timeout)
        composeTestRule
            .onNodeWithText(TITLE_1)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$TITLE_1 is displayed")
        composeTestRule
            .onNodeWithText(TITLE_2)
            .assertDoesNotExist()
        Log.e("test $testNumber assert", "$TITLE_2 does not exist")
        val indexSong2 = localRepo
            .getSongsByArtistAsList(ARTIST_1)
            .map { it.title }
            .indexOf(TITLE_2) - 3
        composeTestRule
            .onNodeWithTag(SONG_LIST_LAZY_COLUMN)
            .performScrollToIndex(indexSong2)
        Log.e("test $testNumber scroll", "songList to index $indexSong2")
        composeTestRule.waitFor(timeout)
        composeTestRule
            .onNodeWithText(TITLE_2)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$TITLE_2 is displayed")

        composeTestRule
            .onNodeWithTag(DRAWER_BUTTON_MAIN)
            .performClick()
        composeTestRule.waitFor(timeout)
        Log.e("test $testNumber click", DRAWER_BUTTON_MAIN)
        val index2 = artists.indexOf(ARTIST_2) - 3
        composeTestRule
            .onNodeWithTag(MENU_LAZY_COLUMN)
            .performScrollToIndex(index2)
        Log.e("test $testNumber scroll", "menu to index $index2")
        composeTestRule.waitFor(timeout)
        composeTestRule
            .onNodeWithText(ARTIST_2)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$ARTIST_2 is displayed")
        composeTestRule
            .onNodeWithText(ARTIST_2)
            .performClick()
        composeTestRule.waitFor(timeout)
        composeTestRule
            .onNodeWithText(TITLE_3)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$TITLE_3 is displayed")
        composeTestRule
            .onNodeWithText(TITLE_4)
            .assertDoesNotExist()
        Log.e("test $testNumber assert", "$TITLE_4 does not exist")
        val indexSong4 = localRepo
            .getSongsByArtistAsList(ARTIST_2)
            .map { it.title }
            .indexOf(TITLE_4) - 3
        composeTestRule
            .onNodeWithTag(SONG_LIST_LAZY_COLUMN)
            .performScrollToIndex(indexSong4)
        Log.e("test $testNumber scroll", "songList to index $indexSong4")
        composeTestRule.waitFor(timeout)
        composeTestRule
            .onNodeWithText(TITLE_4)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$TITLE_4 is displayed")
    }

    @Test
    fun test2_songText() {
        val testNumber = 2

        val artists = localRepo.getArtistsAsList()

        composeTestRule
            .onNodeWithTag(DRAWER_BUTTON_MAIN)
            .performClick()
        Log.e("test $testNumber click", DRAWER_BUTTON_MAIN)
        composeTestRule.waitFor(timeout)

        val index1 = artists.indexOf(ARTIST_1) - 3
        composeTestRule
            .onNodeWithTag(MENU_LAZY_COLUMN)
            .performScrollToIndex(index1)
        Log.e("test $testNumber scroll", "menu to index $index1")
        composeTestRule.waitFor(timeout)
        composeTestRule
            .onNodeWithText(ARTIST_1)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$ARTIST_1 is displayed")
        composeTestRule
            .onNodeWithText(ARTIST_1)
            .performClick()
        Log.e("test $testNumber click", ARTIST_1)
        composeTestRule.waitFor(timeout)

        val songs = localRepo.getSongsByArtistAsList(ARTIST_1)
        val songIndex1 = songs.indexOfFirst { it.title == TITLE_5 }
        val song1 = songs[songIndex1]
        composeTestRule
            .onNodeWithTag(SONG_LIST_LAZY_COLUMN)
            .performScrollToIndex(songIndex1 - 3)
        Log.e("test $testNumber scroll", "songList to index $songIndex1 - 3")
        composeTestRule.waitFor(timeout)

        composeTestRule
            .onNodeWithText(TITLE_5)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$TITLE_5 is displayed")
        composeTestRule
            .onNodeWithText(TITLE_5)
            .performClick()
        composeTestRule.waitFor(timeout)
        Log.e("test $testNumber click", TITLE_5)

        composeTestRule
            .onNodeWithText("${song1.title} (${song1.artist})")
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "song title with artist is displayed")
        composeTestRule
            .onNodeWithTag(SONG_TEXT_VIEWER)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$SONG_TEXT_VIEWER is displayed")
        composeTestRule
            .onNodeWithTag(SONG_TEXT_EDITOR)
            .assertDoesNotExist()
        Log.e("test $testNumber assert", "$SONG_TEXT_EDITOR does not exist")
        composeTestRule
            .onNodeWithTag(EDIT_BUTTON)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$EDIT_BUTTON is displayed")
        composeTestRule
            .onNodeWithTag(SAVE_BUTTON)
            .assertDoesNotExist()
        Log.e("test $testNumber assert", "$SAVE_BUTTON does not exist")
        composeTestRule
            .onNodeWithTag(EDIT_BUTTON)
            .performClick()
        Log.e("test $testNumber click", "$EDIT_BUTTON click")
        composeTestRule.waitFor(timeout)

        composeTestRule
            .onNodeWithTag(SONG_TEXT_EDITOR)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$SONG_TEXT_EDITOR is displayed")
        composeTestRule
            .onNodeWithTag(SONG_TEXT_VIEWER)
            .assertDoesNotExist()
        Log.e("test $testNumber assert", "$SONG_TEXT_VIEWER does not exist")
        composeTestRule
            .onNodeWithTag(SONG_TEXT_EDITOR)
            .assertTextEquals(song1.text)
        Log.e("test $testNumber assert", "$SONG_TEXT_EDITOR is displayed")
        composeTestRule
            .onNodeWithTag(SAVE_BUTTON)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$SAVE_BUTTON is displayed")
        composeTestRule
            .onNodeWithTag(EDIT_BUTTON)
            .assertDoesNotExist()
        Log.e("test $testNumber assert", "$EDIT_BUTTON does not exist")
        composeTestRule
            .onNodeWithTag(SAVE_BUTTON)
            .performClick()
        Log.e("test $testNumber click", "$SAVE_BUTTON click")
        composeTestRule.waitFor(timeout)

        composeTestRule
            .onNodeWithTag(SONG_TEXT_VIEWER)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$SONG_TEXT_VIEWER is displayed")
        composeTestRule
            .onNodeWithTag(SONG_TEXT_EDITOR)
            .assertDoesNotExist()
        Log.e("test $testNumber assert", "$SONG_TEXT_EDITOR does not exist")
        composeTestRule
            .onNodeWithTag(EDIT_BUTTON)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$EDIT_BUTTON is displayed")
        composeTestRule
            .onNodeWithTag(SAVE_BUTTON)
            .assertDoesNotExist()
        Log.e("test $testNumber assert", "$SAVE_BUTTON does not exist")

        composeTestRule
            .onAllNodesWithTag(MUSIC_BUTTON)
            .assertCountEquals(2)
        Log.e("test $testNumber assert", "$MUSIC_BUTTON count is 2")

        val song2 = songs[songIndex1 + 1]

        composeTestRule
            .onNodeWithTag(RIGHT_BUTTON)
            .performClick()
        Log.e("test $testNumber click", RIGHT_BUTTON)
        composeTestRule.waitFor(timeout)
        composeTestRule
            .onNodeWithText("${song2.title} (${song2.artist})")
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "song2 title with artist is displayed")

        composeTestRule
            .onNodeWithTag(LEFT_BUTTON)
            .performClick()
        Log.e("test $testNumber click", LEFT_BUTTON)
        composeTestRule.waitFor(timeout)
        composeTestRule
            .onNodeWithText("${song1.title} (${song1.artist})")
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "song1 title with artist is displayed")

        if (song1.favorite) {
            composeTestRule
                .onNodeWithTag(DELETE_FROM_FAVORITE_BUTTON)
                .performClick()
            Log.e("test $testNumber click", "$DELETE_FROM_FAVORITE_BUTTON click")
        }
        composeTestRule.waitFor(timeout)
        composeTestRule
            .onNodeWithTag(DELETE_FROM_FAVORITE_BUTTON)
            .assertDoesNotExist()
        Log.e("test $testNumber assert", "$DELETE_FROM_FAVORITE_BUTTON does not exist")
        composeTestRule
            .onNodeWithTag(ADD_TO_FAVORITE_BUTTON)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$ADD_TO_FAVORITE_BUTTON is displayed")
        composeTestRule
            .onNodeWithTag(ADD_TO_FAVORITE_BUTTON)
            .performClick()
        Log.e("test $testNumber click", ADD_TO_FAVORITE_BUTTON)
        composeTestRule.waitFor(timeout)

        composeTestRule
            .onNodeWithTag(ADD_TO_FAVORITE_BUTTON)
            .assertDoesNotExist()
        Log.e("test $testNumber assert", "$ADD_TO_FAVORITE_BUTTON does not exist")
        composeTestRule
            .onNodeWithTag(DELETE_FROM_FAVORITE_BUTTON)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$DELETE_FROM_FAVORITE_BUTTON is displayed")
        composeTestRule
            .onNodeWithTag(DELETE_FROM_FAVORITE_BUTTON)
            .performClick()
        Log.e("test $testNumber click", DELETE_FROM_FAVORITE_BUTTON)
        composeTestRule.waitFor(timeout)

        composeTestRule
            .onNodeWithTag(DELETE_FROM_FAVORITE_BUTTON)
            .assertDoesNotExist()
        Log.e("test $testNumber assert", "$DELETE_FROM_FAVORITE_BUTTON does not exist")
        composeTestRule
            .onNodeWithTag(ADD_TO_FAVORITE_BUTTON)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$ADD_TO_FAVORITE_BUTTON is displayed")

        composeTestRule
            .onNodeWithTag(UPLOAD_BUTTON)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$UPLOAD_BUTTON is displayed")

        composeTestRule
            .onNodeWithTag(WARNING_BUTTON)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$WARNING_BUTTON is displayed")
        composeTestRule
            .onNodeWithTag(WARNING_BUTTON)
            .performClick()
        Log.e("test $testNumber click", WARNING_BUTTON)
        composeTestRule.waitFor(timeout)

        composeTestRule
            .onNodeWithText(stringConst.sendWarningText)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${stringConst.sendWarningText} is displayed")
        composeTestRule
            .onNodeWithText(stringConst.ok)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${stringConst.ok} is displayed")
        composeTestRule
            .onNodeWithText(stringConst.cancel)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$stringConst.cancel is displayed")
        composeTestRule
            .onNodeWithText(stringConst.cancel)
            .performClick()
        Log.e("test $testNumber click", stringConst.cancel)
        composeTestRule.waitFor(timeout)

        composeTestRule
            .onNodeWithTag(TRASH_BUTTON)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$TRASH_BUTTON is displayed")
        composeTestRule
            .onNodeWithTag(TRASH_BUTTON)
            .performClick()
        Log.e("test $testNumber click", TRASH_BUTTON)
        composeTestRule.waitFor(timeout)

        composeTestRule
            .onNodeWithText(stringConst.songToTrashTitle)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${stringConst.songToTrashTitle} is displayed")
        composeTestRule
            .onNodeWithText(stringConst.songToTrashMessage)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${stringConst.songToTrashMessage} is displayed")
        composeTestRule
            .onNodeWithText(stringConst.ok)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${stringConst.ok} is displayed")
        composeTestRule
            .onNodeWithText(stringConst.cancel)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${stringConst.cancel} is displayed")
        composeTestRule
            .onNodeWithText(stringConst.cancel)
            .performClick()
        Log.e("test $testNumber click", stringConst.cancel)
    }

    @Test
    fun test3_cloudSearch() {
        val testNumber = 3

        cloudRepository.apply {
            var list = search("", OrderBy.BY_ID_DESC)
            var titleList = list.map { "${it.artist} - ${it.title}" }
            Log.e("test $testNumber list", titleList.toString())

            composeTestRule
                .onNodeWithTag(DRAWER_BUTTON_MAIN)
                .performClick()
            Log.e("test $testNumber click", DRAWER_BUTTON_MAIN)
            composeTestRule.waitFor(timeout)
            composeTestRule
                .onNodeWithText(ARTIST_CLOUD_SONGS)
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "$ARTIST_CLOUD_SONGS is displayed")
            composeTestRule
                .onNodeWithText(ARTIST_CLOUD_SONGS)
                .performClick()
            Log.e("test $testNumber click", ARTIST_CLOUD_SONGS)
            composeTestRule.waitFor(timeout)
            composeTestRule
                .onNodeWithText(OrderBy.BY_ID_DESC.orderByRus)
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "${OrderBy.BY_ID_DESC.orderByRus} is displayed")
            composeTestRule
                .onNodeWithText(list[2].visibleTitleWithRating)
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "${list[2].visibleTitleWithRating} is displayed")
            isOnline = false
            composeTestRule
                .onNodeWithTag(SEARCH_BUTTON)
                .performClick()
            Log.e("test $testNumber click", SEARCH_BUTTON)
            composeTestRule.waitFor(timeout)
            composeTestRule
                .onNodeWithText(stringConst.fetchDataError)
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "${stringConst.fetchDataError} получения данных is displayed")
            isOnline = true
            composeTestRule
                .onNodeWithTag(TEXT_FIELD_SEARCH_FOR)
                .performTextReplacement("Ло")
            Log.e("test $testNumber input", "Ло")
            composeTestRule
                .onNodeWithTag(SEARCH_BUTTON)
                .performClick()
            Log.e("test $testNumber click", SEARCH_BUTTON)
            composeTestRule.waitFor(timeout)

            list = search("Ло", OrderBy.BY_ID_DESC)
            titleList = list.map { "${it.artist} - ${it.title}" }
            Log.e("test $testNumber list", titleList.toString())

            composeTestRule
                .onNodeWithText(list[2].visibleTitleWithRating)
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "${list[2].visibleTitleWithRating} is displayed")
            composeTestRule
                .onNodeWithText(OrderBy.BY_ID_DESC.orderByRus)
                .performClick()
            Log.e("test $testNumber click", OrderBy.BY_ID_DESC.orderByRus)
            composeTestRule.waitFor(timeout)
            composeTestRule
                .onAllNodesWithText(OrderBy.BY_ID_DESC.orderByRus)
                .assertCountEquals(2)
            Log.e("test $testNumber assert", "${OrderBy.BY_ID_DESC.orderByRus} count is 2")
            composeTestRule
                .onNodeWithText(OrderBy.BY_ARTIST.orderByRus)
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "${OrderBy.BY_ARTIST.orderByRus} is displayed")
            composeTestRule
                .onNodeWithText(OrderBy.BY_TITLE.orderByRus)
                .performClick()
            Log.e("test $testNumber click", OrderBy.BY_TITLE.orderByRus)
            composeTestRule.waitFor(timeout)

            list = search("Ло", OrderBy.BY_TITLE)
            titleList = list.map { "${it.artist} - ${it.title}" }
            Log.e("test $testNumber list", titleList.toString())

            composeTestRule
                .onNodeWithText(list[2].visibleTitleWithRating)
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "${list[2].visibleTitleWithRating} is displayed")

            Log.e("test $testNumber assert", "${list[2].visibleTitleWithRating} is displayed")
            composeTestRule
                .onNodeWithText(OrderBy.BY_TITLE.orderByRus)
                .performClick()
            Log.e("test $testNumber click", OrderBy.BY_ID_DESC.orderByRus)
            composeTestRule.waitFor(timeout)
            composeTestRule
                .onNodeWithText(OrderBy.BY_ID_DESC.orderByRus)
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "${OrderBy.BY_ID_DESC.orderByRus} is displayed")
            composeTestRule
                .onAllNodesWithText(OrderBy.BY_TITLE.orderByRus)
                .assertCountEquals(2)
            Log.e("test $testNumber assert", "${OrderBy.BY_TITLE.orderByRus} count is 2")
            composeTestRule
                .onNodeWithText(OrderBy.BY_ARTIST.orderByRus)
                .performClick()
            Log.e("test $testNumber click", OrderBy.BY_ARTIST.orderByRus)
            composeTestRule.waitFor(timeout)

            list = search("Ло", OrderBy.BY_ARTIST)
            titleList = list.map { "${it.artist} - ${it.title}" }
            Log.e("test $testNumber list", titleList.toString())

            composeTestRule
                .onNodeWithText(list[2].visibleTitleWithRating)
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "${list[2].visibleTitleWithRating} is displayed")

            composeTestRule
                .onNodeWithTag(TEXT_FIELD_SEARCH_FOR)
                .performTextReplacement("Хзщшг")
            Log.e("test $testNumber input", "Хзщшг")
            composeTestRule
                .onNodeWithTag(SEARCH_BUTTON)
                .performClick()
            Log.e("test $testNumber click", SEARCH_BUTTON)
            composeTestRule.waitFor(timeout)
            composeTestRule
                .onNodeWithText(stringConst.listIsEmpty)
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "${stringConst.listIsEmpty} is displayed")
            composeTestRule
                .onNodeWithTag(SEARCH_BUTTON)
                .performClick()
            Log.e("test $testNumber click", SEARCH_BUTTON)
            composeTestRule.waitFor(timeout)
            composeTestRule
                .onNodeWithText(stringConst.listIsEmpty)
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "${stringConst.listIsEmpty} is displayed")
        }
    }

    @Test
    fun test4_cloudSongText() {
        val testNumber = 4

        cloudRepository.apply {
            val list = search("", OrderBy.BY_ID_DESC)
            val titleList = list.map { "${it.artist} - ${it.title}" }
            Log.e("test $testNumber list", titleList.toString())

            composeTestRule
                .onNodeWithTag(DRAWER_BUTTON_MAIN)
                .performClick()
            Log.e("test $testNumber click", DRAWER_BUTTON_MAIN)
            composeTestRule.waitFor(timeout)
            composeTestRule
                .onNodeWithText(ARTIST_CLOUD_SONGS)
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "$ARTIST_CLOUD_SONGS is displayed")
            composeTestRule
                .onNodeWithText(ARTIST_CLOUD_SONGS)
                .performClick()
            Log.e("test $testNumber click", ARTIST_CLOUD_SONGS)
            composeTestRule.waitFor(timeout)

            composeTestRule
                .onNodeWithText(list[2].visibleTitleWithRating)
                .performClick()
            Log.e("test $testNumber click", list[2].visibleTitleWithRating)
            composeTestRule.waitFor(timeout)
            composeTestRule
                .onNodeWithText(list[2].visibleTitleWithArtistAndRating)
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "${list[2].visibleTitleWithArtistAndRating} is displayed")
            composeTestRule
                .onNodeWithTag(CLOUD_SONG_TEXT_VIEWER)
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "$CLOUD_SONG_TEXT_VIEWER is displayed")

            composeTestRule
                .onAllNodesWithTag(MUSIC_BUTTON)
                .assertCountEquals(2)
            Log.e("test $testNumber assert", "$MUSIC_BUTTON count is 2")

            composeTestRule
                .onNodeWithTag(DOWNLOAD_BUTTON)
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "$DOWNLOAD_BUTTON is displayed")
            composeTestRule
                .onNodeWithTag(DOWNLOAD_BUTTON)
                .performClick()
            Log.e("test $testNumber click", DOWNLOAD_BUTTON)
            composeTestRule.waitFor(timeout)
            assert(
                localRepo
                    .getSongsByArtistAsList(list[2].artist)
                    .map { it.title }
                    .contains(list[2].title)
            )
            Log.e("test $testNumber assert", "${list[2].artist} contains ${list[2].title}")
            assert(
                localRepo
                    .getSongsByArtistAsList(ARTIST_FAVORITE)
                    .map { it.title }
                    .contains(list[2].title)
            )
            Log.e("test $testNumber assert", "$ARTIST_FAVORITE contains ${list[2].title}")

            composeTestRule
                .onNodeWithTag(WARNING_BUTTON)
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "$WARNING_BUTTON is displayed")
            composeTestRule
                .onNodeWithTag(WARNING_BUTTON)
                .performClick()
            Log.e("test $testNumber click", WARNING_BUTTON)
            composeTestRule.waitFor(timeout)

            composeTestRule
                .onNodeWithText(stringConst.sendWarningText)
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "${stringConst.sendWarningText} is displayed")
            composeTestRule
                .onNodeWithText(stringConst.ok)
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "${stringConst.ok} is displayed")
            composeTestRule
                .onNodeWithText(stringConst.cancel)
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "${stringConst.cancel} is displayed")
            composeTestRule
                .onNodeWithText(stringConst.cancel)
                .performClick()
            Log.e("test $testNumber click", stringConst.cancel)
            composeTestRule.waitFor(timeout)

            var cloudSong = list[2]

            composeTestRule
                .onNodeWithTag(LIKE_BUTTON)
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "$LIKE_BUTTON is displayed")
            composeTestRule
                .onNodeWithTag(LIKE_BUTTON)
                .performClick()
            Log.e("test $testNumber click", LIKE_BUTTON)
            composeTestRule.waitFor(timeout)
            composeTestRule
                .onNodeWithText(cloudSong.visibleTitleWithArtistAndRating)
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "${cloudSong.visibleTitleWithArtistAndRating} is displayed")
            assert(cloudSong.likeCount == 1)
            Log.e("test $testNumber assert", "likeCount == 1")

            composeTestRule
                .onNodeWithTag(NUMBER_LABEL)
                .assertTextContains("3 /", substring = true)
            Log.e("test $testNumber assert", "$NUMBER_LABEL contains '3 /'")

            composeTestRule
                .onNodeWithTag(RIGHT_BUTTON)
                .performClick()
            Log.e("test $testNumber click", RIGHT_BUTTON)
            composeTestRule.waitFor(timeout)

            composeTestRule
                .onNodeWithText(list[3].visibleTitleWithArtistAndRating)
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "${list[3].visibleTitleWithArtistAndRating} is displayed")
            composeTestRule
                .onNodeWithTag(NUMBER_LABEL)
                .assertTextContains("4 /", substring = true)
            Log.e("test $testNumber assert", "$NUMBER_LABEL contains '4 /'")

            cloudSong = list[3]

            composeTestRule
                .onNodeWithTag(DISLIKE_BUTTON)
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "$DISLIKE_BUTTON is displayed")
            composeTestRule
                .onNodeWithTag(DISLIKE_BUTTON)
                .performClick()
            Log.e("test $testNumber click", DISLIKE_BUTTON)
            composeTestRule.waitFor(timeout)
            composeTestRule
                .onNodeWithText(cloudSong.visibleTitleWithArtistAndRating)
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "${cloudSong.visibleTitleWithArtistAndRating} is displayed")
            assert(cloudSong.dislikeCount == 1)
            Log.e("test $testNumber assert", "dislikeCount == 1")

            composeTestRule
                .onNodeWithTag(LEFT_BUTTON)
                .performClick()
            Log.e("test $testNumber click", LEFT_BUTTON)
            composeTestRule.waitFor(timeout)

            composeTestRule
                .onNodeWithText(list[2].visibleTitleWithArtistAndRating)
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "${list[2].visibleTitleWithArtistAndRating} is displayed")
            composeTestRule
                .onNodeWithTag(NUMBER_LABEL)
                .assertTextContains("3 /", substring = true)
            Log.e("test $testNumber assert", "$NUMBER_LABEL contains '3 /'")

            composeTestRule
                .onNodeWithTag(BACK_BUTTON)
                .performClick()
            Log.e("test $testNumber click", BACK_BUTTON)
            composeTestRule.waitFor(5000)

            composeTestRule
                .onNodeWithText(list[2].visibleTitleWithRating)
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "${list[2].visibleTitleWithRating} is displayed")
        }
    }

    @Test
    fun test5_addSong() {
        val testNumber = 5

        composeTestRule
            .onNodeWithTag(DRAWER_BUTTON_MAIN)
            .performClick()
        Log.e("test $testNumber click", DRAWER_BUTTON_MAIN)
        composeTestRule.waitFor(timeout)
        composeTestRule
            .onNodeWithText(ARTIST_ADD_SONG)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$ARTIST_ADD_SONG is displayed")
        composeTestRule
            .onNodeWithText(ARTIST_ADD_SONG)
            .performClick()
        Log.e("test $testNumber click", ARTIST_ADD_SONG)
        composeTestRule.waitFor(timeout)

        composeTestRule
            .onNodeWithText(stringConst.save)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${stringConst.save} is displayed")

        composeTestRule
            .onNodeWithTag(TEXT_FIELD_ARTIST)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$TEXT_FIELD_ARTIST is displayed")
        composeTestRule
            .onNodeWithTag(TEXT_FIELD_ARTIST)
            .performTextReplacement(ARTIST_NEW)
        Log.e("test $testNumber input", ARTIST_NEW)
        composeTestRule.waitFor(timeout)

        composeTestRule
            .onNodeWithTag(TEXT_FIELD_TITLE)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$TEXT_FIELD_TITLE is displayed")
        composeTestRule
            .onNodeWithTag(TEXT_FIELD_TITLE)
            .performTextReplacement(TITLE_NEW)
        Log.e("test $testNumber input", TITLE_NEW)
        composeTestRule.waitFor(timeout)

        composeTestRule
            .onNodeWithTag(TEXT_FIELD_TEXT)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$TEXT_FIELD_TEXT is displayed")
        composeTestRule
            .onNodeWithTag(TEXT_FIELD_TEXT)
            .performTextReplacement(TEXT_NEW)
        Log.e("test $testNumber input", TEXT_NEW)
        composeTestRule.waitFor(timeout)

        composeTestRule
            .onNodeWithText(stringConst.save)
            .performClick()
        Log.e("test $testNumber click", stringConst.save)
        composeTestRule.waitFor(timeout)

        composeTestRule
            .onNodeWithText(stringConst.uploadToCloudTitle)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${stringConst.uploadToCloudTitle} is displayed")
        composeTestRule
            .onNodeWithText(stringConst.uploadToCloudMessage)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${stringConst.uploadToCloudMessage} is displayed")
        composeTestRule
            .onNodeWithText(stringConst.ok)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${stringConst.ok} is displayed")
        composeTestRule
            .onNodeWithText(stringConst.cancel)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${stringConst.cancel} is displayed")
        composeTestRule
            .onNodeWithText(stringConst.cancel)
            .performClick()
        Log.e("test $testNumber click", stringConst.cancel)
        composeTestRule.waitFor(timeout)

        composeTestRule
            .onNodeWithText("$TITLE_NEW ($ARTIST_NEW)")
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "'Title (Artist)' is displayed")
        composeTestRule
            .onNodeWithTag(SONG_TEXT_VIEWER)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$SONG_TEXT_VIEWER is displayed")

        val song = localRepo.getSongByArtistAndTitle(ARTIST_NEW, TITLE_NEW)
        assert(song != null)
        song!!.apply {
            assert(text == TEXT_NEW)
        }
        Log.e("test $testNumber assert", "new song exists and text matches")

        composeTestRule
            .onNodeWithTag(TRASH_BUTTON)
            .performClick()
        Log.e("test $testNumber click", TRASH_BUTTON)
        composeTestRule.waitFor(timeout)

        composeTestRule
            .onNodeWithText(stringConst.songToTrashTitle)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${stringConst.songToTrashTitle} is displayed")
        composeTestRule
            .onNodeWithText(stringConst.songToTrashMessage)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${stringConst.songToTrashMessage} is displayed")
        composeTestRule
            .onNodeWithText(stringConst.ok)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${stringConst.ok} is displayed")
        composeTestRule
            .onNodeWithText(stringConst.cancel)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${stringConst.cancel} is displayed")
        composeTestRule
            .onNodeWithText(stringConst.ok)
            .performClick()
        Log.e("test $testNumber click", stringConst.ok)
        composeTestRule.waitFor(timeout)

        composeTestRule
            .onNodeWithText(stringConst.listIsEmpty)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${stringConst.listIsEmpty} is displayed")
        val artists = localRepo.getArtistsAsList()
        assert(!artists.contains(ARTIST_NEW))
        Log.e("test $testNumber assert", "new artist deleted")
    }

    @Test
    fun test6_settings() {
        val testNumber = 6

        composeTestRule
            .onNodeWithTag(SETTINGS_BUTTON)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$SETTINGS_BUTTON is displayed")
        composeTestRule
            .onNodeWithTag(SETTINGS_BUTTON)
            .performClick()
        Log.e("test $testNumber click", SETTINGS_BUTTON)
        composeTestRule.waitFor(timeout)

        composeTestRule
            .onNodeWithText(stringConst.saveAndRestart)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${stringConst.saveAndRestart} is displayed")

        composeTestRule
            .onNodeWithText(stringConst.theme)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${stringConst.theme} is displayed")
        composeTestRule
            .onNodeWithTag(THEME_SPINNER)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$THEME_SPINNER is displayed")
        composeTestRule
            .onNodeWithTag(THEME_SPINNER)
            .assertTextEquals(stringConst.themeList[settingsRepository.theme.ordinal])
        Log.e(
            "test $testNumber assert",
            "$THEME_SPINNER text is " +
                    stringConst.themeList[settingsRepository.theme.ordinal]
        )

        composeTestRule
            .onNodeWithText(stringConst.fontScale)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${stringConst.fontScale} is displayed")
        composeTestRule
            .onNodeWithTag(FONT_SCALE_SPINNER)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$FONT_SCALE_SPINNER is displayed")
        composeTestRule
            .onNodeWithTag(FONT_SCALE_SPINNER)
            .assertTextEquals(stringConst.fontScaleList[settingsRepository.commonFontScaleEnum.ordinal])
        Log.e(
            "test $testNumber assert",
            "$FONT_SCALE_SPINNER text is " +
                    stringConst.fontScaleList[settingsRepository.commonFontScaleEnum.ordinal]
        )

        composeTestRule
            .onNodeWithText(stringConst.defArtist)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${stringConst.defArtist} is displayed")
        composeTestRule
            .onNodeWithTag(DEFAULT_ARTIST_SPINNER)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$DEFAULT_ARTIST_SPINNER is displayed")
        composeTestRule
            .onNodeWithTag(DEFAULT_ARTIST_SPINNER)
            .assertTextEquals(settingsRepository.defaultArtist)
        Log.e(
            "test $testNumber assert",
            "$DEFAULT_ARTIST_SPINNER text is ${settingsRepository.defaultArtist}"
        )

        composeTestRule
            .onNodeWithText(stringConst.orientFix)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${stringConst.orientFix} is displayed")
        composeTestRule
            .onNodeWithTag(ORIENTATION_SPINNER)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$ORIENTATION_SPINNER is displayed")
        composeTestRule
            .onNodeWithTag(ORIENTATION_SPINNER)
            .assertTextEquals(stringConst.orientationList[settingsRepository.orientation.ordinal])
        Log.e(
            "test $testNumber assert",
            "$ORIENTATION_SPINNER text is " +
                    stringConst.orientationList[settingsRepository.orientation.ordinal]
        )

        composeTestRule
            .onNodeWithText(stringConst.listenToMusic)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${stringConst.listenToMusic} is displayed")
        composeTestRule
            .onNodeWithTag(LISTEN_TO_MUSIC_VARIANT_SPINNER)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$LISTEN_TO_MUSIC_VARIANT_SPINNER is displayed")
        composeTestRule
            .onNodeWithTag(LISTEN_TO_MUSIC_VARIANT_SPINNER)
            .assertTextEquals(
                stringConst.listenToMusicVariants[settingsRepository.listenToMusicVariant.ordinal]
            )
        Log.e(
            "test $testNumber assert",
            "$LISTEN_TO_MUSIC_VARIANT_SPINNER text is " +
                    stringConst.listenToMusicVariants[settingsRepository.listenToMusicVariant.ordinal]
        )

        composeTestRule
            .onNodeWithText(stringConst.scrollSpeed)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${stringConst.scrollSpeed} is displayed")
        composeTestRule
            .onNodeWithTag(TEXT_FIELD_SCROLL_SPEED)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$TEXT_FIELD_SCROLL_SPEED is displayed")
        composeTestRule
            .onNodeWithTag(TEXT_FIELD_SCROLL_SPEED)
            .assertTextEquals(settingsRepository.scrollSpeed.toString())
        Log.e(
            "test $testNumber assert",
            "$TEXT_FIELD_SCROLL_SPEED text is ${settingsRepository.scrollSpeed}"
        )
    }

    @Test
    fun test7_donation() {
        val testNumber = 7

        composeTestRule
            .onNodeWithTag(DRAWER_BUTTON_MAIN)
            .performClick()
        Log.e("test $testNumber click", DRAWER_BUTTON_MAIN)
        composeTestRule.waitFor(timeout)
        composeTestRule
            .onNodeWithText(ARTIST_DONATION)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$ARTIST_DONATION is displayed")
        composeTestRule
            .onNodeWithText(ARTIST_DONATION)
            .performClick()
        Log.e("test $testNumber click", ARTIST_DONATION)
        composeTestRule.waitFor(timeout)

        DONATIONS
            .map { donationLabel(it) }
            .forEach {
            composeTestRule
                .onNodeWithText(it)
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "$it is displayed")
        }
    }

    @Test
    fun test8_addArtist() {
        val testNumber = 8

        composeTestRule
            .onNodeWithTag(DRAWER_BUTTON_MAIN)
            .performClick()
        Log.e("test $testNumber click", DRAWER_BUTTON_MAIN)
        composeTestRule.waitFor(timeout)
        composeTestRule
            .onNodeWithText(ARTIST_ADD_ARTIST)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$ARTIST_ADD_ARTIST is displayed")
        composeTestRule
            .onNodeWithText(ARTIST_ADD_ARTIST)
            .performClick()
        Log.e("test $testNumber click", ARTIST_ADD_ARTIST)
        composeTestRule.waitFor(timeout)

        composeTestRule
            .onNodeWithText(stringConst.addArtistManual)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${stringConst.addArtistManual} is displayed")
        composeTestRule
            .onNodeWithText(stringConst.choose)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${stringConst.choose} is displayed")
    }
}

const val timeout = 1200L

fun AndroidComposeTestRule<out TestRule, out ComponentActivity>.waitFor(time: Long) {
    try {
        waitUntil(time) { false }
    } catch (e: ComposeTimeoutException) {
        Log.e("test wait", "timeout")
    }
}

class StringConst @Inject constructor(
    @ApplicationContext context: Context
) {
    val ok = context.getString(R.string.ok)
    val cancel = context.getString(R.string.cancel)
    val listIsEmpty = context.getString(R.string.label_placeholder)
    val fetchDataError = context.getString(R.string.label_error_placeholder)
    val sendWarningText = context.getString(R.string.send_warning_text)
    val songToTrashTitle = context.getString(R.string.dialog_song_to_trash_title)
    val songToTrashMessage = context.getString(R.string.dialog_song_to_trash_message)
    val save = context.getString(R.string.save)
    val uploadToCloudTitle = context.getString(R.string.dialog_upload_to_cloud_title)
    val uploadToCloudMessage = context.getString(R.string.dialog_upload_to_cloud_message)
    val theme = context.getString(R.string.theme)
    val fontScale = context.getString(R.string.font_scale)
    val defArtist = context.getString(R.string.def_artist)
    val orientFix = context.getString(R.string.orient_fix)
    val listenToMusic = context.getString(R.string.listen_to_music)
    val scrollSpeed = context.getString(R.string.scroll_speed)
    val saveAndRestart = context.getString(R.string.save_and_restart)
    val addArtistManual = context.getString(R.string.add_artist_manual)
    val choose = context.getString(R.string.choose)

    val themeList = context.resources.getStringArray(R.array.theme_list)
    val fontScaleList = context.resources.getStringArray(R.array.font_scale_list)
    val orientationList = context.resources.getStringArray(R.array.orientation_list)
    val listenToMusicVariants = context.resources.getStringArray(R.array.listen_to_music_variants)
}