package jatx.russianrocksongbook

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import jatx.russianrocksongbook.model.data.*
import jatx.russianrocksongbook.model.data.impl.TestAPIAdapterImpl
import jatx.russianrocksongbook.model.preferences.ARTIST_KINO
import jatx.russianrocksongbook.model.preferences.Settings
import jatx.russianrocksongbook.model.version.Version
import jatx.russianrocksongbook.view.CurrentScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import javax.inject.Inject


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

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
class ExampleInstrumentedTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var songRepo: SongRepository

    @Inject
    lateinit var songBookAPIAdapter: SongBookAPIAdapter

    @Inject
    lateinit var settings: Settings

    @Before
    fun init() {
        Version.isTesting = true
        hiltRule.inject()
        settings.defaultArtist = ARTIST_KINO
        composeTestRule.setContent {
            CurrentScreen()
        }
    }

    @Test
    fun test1_menuAndSongList() {
        val testNumber = 1

        val artists = songRepo.getArtistsAsList()

        composeTestRule
            .onNodeWithTag("drawerButtonMain")
            .performClick()
        Log.e("test $testNumber click", "drawerButtonMain")
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

        val index1 = artists.indexOf("Немного Нервно") - 3
        composeTestRule
            .onNodeWithText("Немного Нервно")
            .assertDoesNotExist()
        Log.e("test $testNumber assert", "Немного Нервно does not exist")
        composeTestRule
            .onNodeWithTag("menuLazyColumn")
            .performScrollToIndex(index1)
        Log.e("test $testNumber scroll", "menu to index $index1")
        composeTestRule.waitFor(timeout)
        composeTestRule
            .onNodeWithText("Немного Нервно")
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "Немного Нервно is displayed")
        composeTestRule
            .onNodeWithText("Немного Нервно")
            .performClick()
        composeTestRule.waitFor(timeout)
        composeTestRule
            .onNodeWithText("Santa Maria")
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "Santa Maria is displayed")
        composeTestRule
            .onNodeWithText("Яблочный остров")
            .assertDoesNotExist()
        Log.e("test $testNumber assert", "Яблочный остров does not exist")

        composeTestRule
            .onNodeWithTag("drawerButtonMain")
            .performClick()
        composeTestRule.waitFor(timeout)
        Log.e("test $testNumber click", "drawerButtonMain")
        val index2 = artists.indexOf("Чайф") - 3
        composeTestRule
            .onNodeWithTag("menuLazyColumn")
            .performScrollToIndex(index2)
        Log.e("test $testNumber scroll", "menu to index $index2")
        composeTestRule.waitFor(timeout)
        composeTestRule
            .onNodeWithText("Чайф")
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "Чайф is displayed")
        composeTestRule
            .onNodeWithText("Чайф")
            .performClick()
        composeTestRule.waitFor(timeout)
        composeTestRule
            .onNodeWithText("17 лет")
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "17 лет is displayed")
        composeTestRule
            .onNodeWithText("Поплачь о нем")
            .assertDoesNotExist()
        Log.e("test $testNumber assert", "Поплачь о нем does not exist")
    }

    @Test
    fun test2_songText() {
        val testNumber = 2

        val artists = songRepo.getArtistsAsList()

        composeTestRule
            .onNodeWithTag("drawerButtonMain")
            .performClick()
        Log.e("test $testNumber click", "drawerButtonMain")
        composeTestRule.waitFor(timeout)

        val index1 = artists.indexOf("Немного Нервно") - 3
        composeTestRule
            .onNodeWithTag("menuLazyColumn")
            .performScrollToIndex(index1)
        Log.e("test $testNumber scroll", "menu to index $index1")
        composeTestRule.waitFor(timeout)
        composeTestRule
            .onNodeWithText("Немного Нервно")
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "Немного Нервно is displayed")
        composeTestRule
            .onNodeWithText("Немного Нервно")
            .performClick()
        Log.e("test $testNumber click", "Немного Нервно")
        composeTestRule.waitFor(timeout)

        val songs = songRepo.getSongsByArtistAsList("Немного Нервно")
        val songIndex1 = songs.indexOfFirst { it.title == "Над мертвым городом сон" }
        val song1 = songs[songIndex1]
        composeTestRule
            .onNodeWithTag("songListLazyColumn")
            .performScrollToIndex(songIndex1 - 3)
        Log.e("test $testNumber scroll", "songList to index $songIndex1 - 3")
        composeTestRule.waitFor(timeout)

        composeTestRule
            .onNodeWithText("Над мертвым городом сон")
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "Над мертвым городом сон is displayed")
        composeTestRule
            .onNodeWithText("Над мертвым городом сон")
            .performClick()
        composeTestRule.waitFor(timeout)
        Log.e("test $testNumber click", "Над мертвым городом сон")

        composeTestRule
            .onNodeWithText("${song1.title} (${song1.artist})")
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "song title with artist is displayed")
        composeTestRule
            .onNodeWithTag("songTextViewer")
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "viewer is displayed")
        composeTestRule
            .onNodeWithTag("songTextEditor")
            .assertDoesNotExist()
        Log.e("test $testNumber assert", "editor does not exist")
        composeTestRule
            .onNodeWithTag("editButton")
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "editButton is displayed")
        composeTestRule
            .onNodeWithTag("saveButton")
            .assertDoesNotExist()
        Log.e("test $testNumber assert", "saveButton does not exist")
        composeTestRule
            .onNodeWithTag("editButton")
            .performClick()
        Log.e("test $testNumber click", "editButton click")
        composeTestRule.waitFor(timeout)

        composeTestRule
            .onNodeWithTag("songTextEditor")
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "editor is displayed")
        composeTestRule
            .onNodeWithTag("songTextViewer")
            .assertDoesNotExist()
        Log.e("test $testNumber assert", "viewer does not exist")
        composeTestRule
            .onNodeWithTag("songTextEditor")
            .assertTextEquals(song1.text)
        Log.e("test $testNumber assert", "song text is displayed")
        composeTestRule
            .onNodeWithTag("saveButton")
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "saveButton is displayed")
        composeTestRule
            .onNodeWithTag("editButton")
            .assertDoesNotExist()
        Log.e("test $testNumber assert", "editButton does not exist")
        composeTestRule
            .onNodeWithTag("saveButton")
            .performClick()
        Log.e("test $testNumber click", "saveButton click")
        composeTestRule.waitFor(timeout)

        composeTestRule
            .onNodeWithTag("songTextViewer")
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "viewer is displayed")
        composeTestRule
            .onNodeWithTag("songTextEditor")
            .assertDoesNotExist()
        Log.e("test $testNumber assert", "editor does not exist")
        composeTestRule
            .onNodeWithTag("editButton")
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "editButton is displayed")
        composeTestRule
            .onNodeWithTag("saveButton")
            .assertDoesNotExist()
        Log.e("test $testNumber assert", "saveButton does not exist")

        composeTestRule
            .onAllNodesWithTag("musicButton")
            .assertCountEquals(2)
        Log.e("test $testNumber assert", "musicButton count is 2")

        val song2 = songs[songIndex1 + 1]

        composeTestRule
            .onNodeWithTag("rightButton")
            .performClick()
        Log.e("test $testNumber click", "rightButton click")
        composeTestRule.waitFor(timeout)
        composeTestRule
            .onNodeWithText("${song2.title} (${song2.artist})")
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "song2 title with artist is displayed")

        composeTestRule
            .onNodeWithTag("leftButton")
            .performClick()
        Log.e("test $testNumber click", "leftButton click")
        composeTestRule.waitFor(timeout)
        composeTestRule
            .onNodeWithText("${song1.title} (${song1.artist})")
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "song1 title with artist is displayed")

        if (song1.favorite) {
            composeTestRule
                .onNodeWithTag("deleteFromFavoriteButton")
                .performClick()
            Log.e("test $testNumber click", "deleteFromFavoriteButton click")
        }
        composeTestRule.waitFor(timeout)
        composeTestRule
            .onNodeWithTag("deleteFromFavoriteButton")
            .assertDoesNotExist()
        Log.e("test $testNumber assert", "deleteFromFavoriteButton does not exist")
        composeTestRule
            .onNodeWithTag("addToFavoriteButton")
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "addToFavoriteButton is displayed")
        composeTestRule
            .onNodeWithTag("addToFavoriteButton")
            .performClick()
        Log.e("test $testNumber click", "addToFavoriteButton click")
        composeTestRule.waitFor(timeout)

        composeTestRule
            .onNodeWithTag("addToFavoriteButton")
            .assertDoesNotExist()
        Log.e("test $testNumber assert", "addToFavoriteButton does not exist")
        composeTestRule
            .onNodeWithTag("deleteFromFavoriteButton")
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "deleteFromFavoriteButton is displayed")
        composeTestRule
            .onNodeWithTag("deleteFromFavoriteButton")
            .performClick()
        Log.e("test $testNumber click", "deleteFromFavoriteButton click")
        composeTestRule.waitFor(timeout)

        composeTestRule
            .onNodeWithTag("deleteFromFavoriteButton")
            .assertDoesNotExist()
        Log.e("test $testNumber assert", "deleteFromFavoriteButton does not exist")
        composeTestRule
            .onNodeWithTag("addToFavoriteButton")
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "addToFavoriteButton is displayed")

        composeTestRule
            .onNodeWithTag("uploadButton")
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "uploadButton is displayed")

        composeTestRule
            .onNodeWithTag("warningButton")
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "warningButton is displayed")
        composeTestRule
            .onNodeWithTag("warningButton")
            .performClick()
        Log.e("test $testNumber click", "warningButton")
        composeTestRule.waitFor(timeout)

        composeTestRule
            .onNodeWithText("Ок")
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "Ок is displayed")
        composeTestRule
            .onNodeWithText("Отмена")
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "Отмена is displayed")
        composeTestRule
            .onNodeWithText("Отмена")
            .performClick()
        Log.e("test $testNumber click", "Отмена")
        composeTestRule.waitFor(timeout)

        composeTestRule
            .onNodeWithTag("trashButton")
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "trashButton is displayed")
        composeTestRule
            .onNodeWithTag("trashButton")
            .performClick()
        Log.e("test $testNumber click", "trashButton")
        composeTestRule.waitFor(timeout)

        composeTestRule
            .onNodeWithText("Ок")
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "Ок is displayed")
        composeTestRule
            .onNodeWithText("Отмена")
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "Отмена is displayed")
        composeTestRule
            .onNodeWithText("Отмена")
            .performClick()
        Log.e("test $testNumber click", "Отмена")
    }

    @Test
    fun test3_cloudSearch() {
        val testNumber = 3

        (songBookAPIAdapter as? TestAPIAdapterImpl)?.apply {
            var list = search("", OrderBy.BY_ID_DESC)
            var titleList = list.map { "${it.artist} - ${it.title}" }
            Log.e("test $testNumber list", titleList.toString())

            composeTestRule
                .onNodeWithTag("drawerButtonMain")
                .performClick()
            Log.e("test $testNumber click", "drawerButtonMain")
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
                .onNodeWithText("Последние добавленные")
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "Последние добавленные is displayed")
            composeTestRule
                .onNodeWithText(list[2].visibleTitleWithRating)
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "${list[2].visibleTitleWithRating} is displayed")
            isOnline = false
            composeTestRule
                .onNodeWithTag("searchButton")
                .performClick()
            Log.e("test $testNumber click", "searchButton")
            composeTestRule.waitFor(timeout)
            composeTestRule
                .onNodeWithText("Ошибка получения данных")
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "Ошибка получения данных is displayed")
            isOnline = true
            composeTestRule
                .onNodeWithTag("searchForTextField")
                .performTextReplacement("Ло")
            Log.e("test $testNumber input", "Ло")
            composeTestRule
                .onNodeWithTag("searchButton")
                .performClick()
            Log.e("test $testNumber click", "searchButton")
            composeTestRule.waitFor(timeout)

            list = search("Ло", OrderBy.BY_ID_DESC)
            titleList = list.map { "${it.artist} - ${it.title}" }
            Log.e("test $testNumber list", titleList.toString())

            composeTestRule
                .onNodeWithText(list[2].visibleTitleWithRating)
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "${list[2].visibleTitleWithRating} is displayed")
            composeTestRule
                .onNodeWithText("Последние добавленные")
                .performClick()
            Log.e("test $testNumber click", "Последние добавленные")
            composeTestRule.waitFor(timeout)
            composeTestRule
                .onNodeWithText("По исполнителю")
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "По исполнителю is displayed")
            composeTestRule
                .onNodeWithText("По названию")
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "По названию is displayed")
            composeTestRule
                .onNodeWithText("По названию")
                .performClick()
            Log.e("test $testNumber click", "По названию")
            composeTestRule.waitFor(timeout)

            list = search("Ло", OrderBy.BY_TITLE)
            titleList = list.map { "${it.artist} - ${it.title}" }
            Log.e("test $testNumber list", titleList.toString())

            composeTestRule
                .onNodeWithText(list[2].visibleTitleWithRating)
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "${list[2].visibleTitleWithRating} is displayed")

            composeTestRule
                .onNodeWithTag("searchForTextField")
                .performTextReplacement("Хзщшг")
            Log.e("test $testNumber input", "Хзщшг")
            composeTestRule
                .onNodeWithTag("searchButton")
                .performClick()
            Log.e("test $testNumber click", "searchButton")
            composeTestRule.waitFor(timeout)
            composeTestRule
                .onNodeWithTag("searchProgress")
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "searchProgress is displayed")
            composeTestRule.waitFor(8000L)
            composeTestRule
                .onNodeWithText("Список пуст")
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "Список пуст is displayed")
        }
    }

    @Test
    fun test4_cloudSongText() {
        val testNumber = 4

        (songBookAPIAdapter as? TestAPIAdapterImpl)?.apply {
            var list = search("", OrderBy.BY_ID_DESC)
            var titleList = list.map { "${it.artist} - ${it.title}" }
            Log.e("test $testNumber list", titleList.toString())

            composeTestRule
                .onNodeWithTag("drawerButtonMain")
                .performClick()
            Log.e("test $testNumber click", "drawerButtonMain")
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
                .onNodeWithTag("cloudSongTextViewer")
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "cloud viewer is displayed")

            composeTestRule
                .onAllNodesWithTag("musicButton")
                .assertCountEquals(2)
            Log.e("test $testNumber assert", "musicButton count is 2")

            composeTestRule
                .onNodeWithTag("downloadButton")
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "downloadButton is displayed")

            composeTestRule
                .onNodeWithTag("warningButton")
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "warningButton is displayed")
            composeTestRule
                .onNodeWithTag("warningButton")
                .performClick()
            Log.e("test $testNumber click", "warningButton")
            composeTestRule.waitFor(timeout)

            composeTestRule
                .onNodeWithText("Ок")
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "Ок is displayed")
            composeTestRule
                .onNodeWithText("Отмена")
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "Отмена is displayed")
            composeTestRule
                .onNodeWithText("Отмена")
                .performClick()
            Log.e("test $testNumber click", "Отмена")
            composeTestRule.waitFor(timeout)

            var cloudSong = list[2]
            cloudSong.likeCount += 1

            composeTestRule
                .onNodeWithTag("likeButton")
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "likeButton is displayed")
            composeTestRule
                .onNodeWithTag("likeButton")
                .performClick()
            Log.e("test $testNumber click", "likeButton")
            composeTestRule.waitFor(timeout)
            composeTestRule
                .onNodeWithText(cloudSong.visibleTitleWithArtistAndRating)
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "${cloudSong.visibleTitleWithArtistAndRating} is displayed")

            composeTestRule
                .onNodeWithTag("numberLabel")
                .assertTextContains("3 /", substring = true)
            Log.e("test $testNumber assert", "numberLabel starts with 3 /")

            composeTestRule
                .onNodeWithTag("rightButton")
                .performClick()
            Log.e("test $testNumber click", "rightButton")
            composeTestRule.waitFor(timeout)

            composeTestRule
                .onNodeWithText(list[3].visibleTitleWithArtistAndRating)
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "${list[3].visibleTitleWithArtistAndRating} is displayed")
            composeTestRule
                .onNodeWithTag("numberLabel")
                .assertTextContains("4 /", substring = true)
            Log.e("test $testNumber assert", "numberLabel starts with 4 /")

            cloudSong = list[3]
            cloudSong.dislikeCount += 1

            composeTestRule
                .onNodeWithTag("dislikeButton")
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "dislikeButton is displayed")
            composeTestRule
                .onNodeWithTag("dislikeButton")
                .performClick()
            Log.e("test $testNumber click", "dislikeButton")
            composeTestRule.waitFor(timeout)
            composeTestRule
                .onNodeWithText(cloudSong.visibleTitleWithArtistAndRating)
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "${cloudSong.visibleTitleWithArtistAndRating} is displayed")

            composeTestRule
                .onNodeWithTag("leftButton")
                .performClick()
            Log.e("test $testNumber click", "leftButton")
            composeTestRule.waitFor(timeout)

            composeTestRule
                .onNodeWithText(list[2].visibleTitleWithArtistAndRating)
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "${list[2].visibleTitleWithArtistAndRating} is displayed")
            composeTestRule
                .onNodeWithTag("numberLabel")
                .assertTextContains("3 /", substring = true)
            Log.e("test $testNumber assert", "numberLabel starts with 3 /")

            composeTestRule
                .onNodeWithTag("backButton")
                .performClick()
            Log.e("test $testNumber click", "backButton")
            composeTestRule.waitFor(5000)

            composeTestRule
                .onNodeWithText(list[2].visibleTitleWithRating)
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "${list[2].visibleTitleWithRating} is displayed")
        }
    }
}

const val timeout = 2000L

fun AndroidComposeTestRule<out TestRule, out ComponentActivity>.waitFor(time: Long) {
    try {
        waitUntil(time) { false }
    } catch (e: ComposeTimeoutException) {
        Log.e("test wait", "timeout")
    }
}