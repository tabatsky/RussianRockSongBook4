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
        hiltRule.inject()
        settings.defaultArtist = ARTIST_KINO
        composeTestRule.setContent {
            CurrentScreen()
        }
    }

    @Test
    fun menuAndSongListTest() {
        val artists = songRepo.getArtistsAsList()

        composeTestRule
            .onNodeWithTag("drawerButtonMain")
            .performClick()
        Log.e("test click", "drawerButtonMain")
        composeTestRule.waitFor(500)
        composeTestRule
            .onNodeWithText(ARTIST_FAVORITE)
            .assertIsDisplayed()
        Log.e("test assert", "$ARTIST_FAVORITE is displayed")
        composeTestRule
            .onNodeWithText(ARTIST_ADD_ARTIST)
            .assertIsDisplayed()
        Log.e("test assert", "$ARTIST_ADD_ARTIST is displayed")
        composeTestRule
            .onNodeWithText(ARTIST_ADD_SONG)
            .assertIsDisplayed()
        Log.e("test assert", "$ARTIST_ADD_SONG is displayed")
        composeTestRule
            .onNodeWithText(ARTIST_CLOUD_SONGS)
            .assertIsDisplayed()
        Log.e("test assert", "$ARTIST_CLOUD_SONGS is displayed")
        composeTestRule
            .onNodeWithText(ARTIST_DONATION)
            .assertIsDisplayed()
        Log.e("test assert", "$ARTIST_DONATION is displayed")

        val index1 = artists.indexOf("Немного Нервно") - 3
        composeTestRule
            .onNodeWithText("Немного Нервно")
            .assertDoesNotExist()
        Log.e("test assert", "Немного Нервно does not exist")
        composeTestRule
            .onNodeWithTag("menuLazyColumn")
            .performScrollToIndex(index1)
        Log.e("test scroll", "menu to index $index1")
        composeTestRule.waitFor(500)
        composeTestRule
            .onNodeWithText("Немного Нервно")
            .assertIsDisplayed()
        Log.e("test assert", "Немного Нервно is displayed")
        composeTestRule
            .onNodeWithText("Немного Нервно")
            .performClick()
        composeTestRule.waitFor(500)
        composeTestRule
            .onNodeWithText("Santa Maria")
            .assertIsDisplayed()
        Log.e("test assert", "Santa Maria is displayed")
        composeTestRule
            .onNodeWithText("Яблочный остров")
            .assertDoesNotExist()
        Log.e("test assert", "Яблочный остров does not exist")

        composeTestRule
            .onNodeWithTag("drawerButtonMain")
            .performClick()
        composeTestRule.waitFor(500)
        Log.e("test click", "drawerButtonMain")
        val index2 = artists.indexOf("Чайф") - 3
        composeTestRule
            .onNodeWithTag("menuLazyColumn")
            .performScrollToIndex(index2)
        Log.e("test scroll", "menu to index $index2")
        composeTestRule.waitFor(500)
        composeTestRule
            .onNodeWithText("Чайф")
            .assertIsDisplayed()
        Log.e("test assert", "Чайф is displayed")
        composeTestRule
            .onNodeWithText("Чайф")
            .performClick()
        composeTestRule.waitFor(500)
        composeTestRule
            .onNodeWithText("17 лет")
            .assertIsDisplayed()
        Log.e("test assert", "17 лет is displayed")
        composeTestRule
            .onNodeWithText("Поплачь о нем")
            .assertDoesNotExist()
        Log.e("test assert", "Поплачь о нем does not exist")
    }

    @Test
    fun songTextTest() {
        val artists = songRepo.getArtistsAsList()

        composeTestRule
            .onNodeWithTag("drawerButtonMain")
            .performClick()
        Log.e("test click", "drawerButtonMain")
        composeTestRule.waitFor(500)

        val index1 = artists.indexOf("Немного Нервно") - 3
        composeTestRule
            .onNodeWithTag("menuLazyColumn")
            .performScrollToIndex(index1)
        Log.e("test scroll", "menu to index $index1")
        composeTestRule.waitFor(500)
        composeTestRule
            .onNodeWithText("Немного Нервно")
            .assertIsDisplayed()
        Log.e("test assert", "Немного Нервно is displayed")
        composeTestRule
            .onNodeWithText("Немного Нервно")
            .performClick()
        Log.e("test click", "Немного Нервно")
        composeTestRule.waitFor(500)

        val songs = songRepo.getSongsByArtistAsList("Немного Нервно")
        val songIndex1 = songs.indexOfFirst { it.title == "Над мертвым городом сон" }
        val song1 = songs[songIndex1]
        composeTestRule
            .onNodeWithTag("songListLazyColumn")
            .performScrollToIndex(songIndex1 - 3)
        Log.e("test scroll", "songList to index $songIndex1 - 3")
        composeTestRule.waitFor(500)

        composeTestRule
            .onNodeWithText("Над мертвым городом сон")
            .assertIsDisplayed()
        Log.e("test assert", "Над мертвым городом сон is displayed")
        composeTestRule
            .onNodeWithText("Над мертвым городом сон")
            .performClick()
        composeTestRule.waitFor(500)
        Log.e("test click", "Над мертвым городом сон")

        composeTestRule
            .onNodeWithText("${song1.title} (${song1.artist})")
            .assertIsDisplayed()
        Log.e("test assert", "song title with artist is displayed")
        composeTestRule
            .onNodeWithTag("songTextViewer")
            .assertIsDisplayed()
        Log.e("test assert", "viewer is displayed")
        composeTestRule
            .onNodeWithTag("songTextEditor")
            .assertDoesNotExist()
        Log.e("test assert", "editor does not exist")
        composeTestRule
            .onNodeWithTag("editButton")
            .assertIsDisplayed()
        Log.e("test assert", "editButton is displayed")
        composeTestRule
            .onNodeWithTag("saveButton")
            .assertDoesNotExist()
        Log.e("test assert", "saveButton does not exist")
        composeTestRule
            .onNodeWithTag("editButton")
            .performClick()
        Log.e("test click", "editButton click")
        composeTestRule.waitFor(500)

        composeTestRule
            .onNodeWithTag("songTextEditor")
            .assertIsDisplayed()
        Log.e("test assert", "editor is displayed")
        composeTestRule
            .onNodeWithTag("songTextViewer")
            .assertDoesNotExist()
        Log.e("test assert", "viewer does not exist")
        composeTestRule
            .onNodeWithTag("songTextEditor")
            .assertTextEquals(song1.text)
        Log.e("test assert", "song text is displayed")
        composeTestRule
            .onNodeWithTag("saveButton")
            .assertIsDisplayed()
        Log.e("test assert", "saveButton is displayed")
        composeTestRule
            .onNodeWithTag("editButton")
            .assertDoesNotExist()
        Log.e("test assert", "editButton does not exist")
        composeTestRule
            .onNodeWithTag("saveButton")
            .performClick()
        Log.e("test click", "saveButton click")
        composeTestRule.waitFor(500)

        composeTestRule
            .onNodeWithTag("songTextViewer")
            .assertIsDisplayed()
        Log.e("test assert", "viewer is displayed")
        composeTestRule
            .onNodeWithTag("songTextEditor")
            .assertDoesNotExist()
        Log.e("test assert", "editor does not exist")
        composeTestRule
            .onNodeWithTag("editButton")
            .assertIsDisplayed()
        Log.e("test assert", "editButton is displayed")
        composeTestRule
            .onNodeWithTag("saveButton")
            .assertDoesNotExist()
        Log.e("test assert", "saveButton does not exist")

        composeTestRule
            .onAllNodesWithTag("musicButton")
            .assertCountEquals(2)
        Log.e("test assert", "musicButton count is 2")

        val song2 = songs[songIndex1 + 1]

        composeTestRule
            .onNodeWithTag("rightButton")
            .performClick()
        Log.e("test click", "rightButton click")
        composeTestRule.waitFor(500)
        composeTestRule
            .onNodeWithText("${song2.title} (${song2.artist})")
            .assertIsDisplayed()
        Log.e("test assert", "song2 title with artist is displayed")

        composeTestRule
            .onNodeWithTag("leftButton")
            .performClick()
        Log.e("test click", "leftButton click")
        composeTestRule.waitFor(500)
        composeTestRule
            .onNodeWithText("${song1.title} (${song1.artist})")
            .assertIsDisplayed()
        Log.e("test assert", "song1 title with artist is displayed")

        if (song1.favorite) {
            composeTestRule
                .onNodeWithTag("deleteFromFavoriteButton")
                .performClick()
            Log.e("test click", "deleteFromFavoriteButton click")
        }
        composeTestRule.waitFor(500)
        composeTestRule
            .onNodeWithTag("deleteFromFavoriteButton")
            .assertDoesNotExist()
        Log.e("test assert", "deleteFromFavoriteButton does not exist")
        composeTestRule
            .onNodeWithTag("addToFavoriteButton")
            .assertIsDisplayed()
        Log.e("test assert", "addToFavoriteButton is displayed")
        composeTestRule
            .onNodeWithTag("addToFavoriteButton")
            .performClick()
        Log.e("test click", "addToFavoriteButton click")
        composeTestRule.waitFor(500)

        composeTestRule
            .onNodeWithTag("addToFavoriteButton")
            .assertDoesNotExist()
        Log.e("test assert", "addToFavoriteButton does not exist")
        composeTestRule
            .onNodeWithTag("deleteFromFavoriteButton")
            .assertIsDisplayed()
        Log.e("test assert", "deleteFromFavoriteButton is displayed")
        composeTestRule
            .onNodeWithTag("deleteFromFavoriteButton")
            .performClick()
        Log.e("test click", "deleteFromFavoriteButton click")
        composeTestRule.waitFor(500)

        composeTestRule
            .onNodeWithTag("deleteFromFavoriteButton")
            .assertDoesNotExist()
        Log.e("test assert", "deleteFromFavoriteButton does not exist")
        composeTestRule
            .onNodeWithTag("addToFavoriteButton")
            .assertIsDisplayed()
        Log.e("test assert", "addToFavoriteButton is displayed")

        composeTestRule
            .onNodeWithTag("uploadButton")
            .assertIsDisplayed()
        Log.e("test assert", "uploadButton is displayed")

        composeTestRule
            .onNodeWithTag("warningButton")
            .assertIsDisplayed()
        Log.e("test assert", "warningButton is displayed")
        composeTestRule
            .onNodeWithTag("warningButton")
            .performClick()
        Log.e("test click", "warningButton")
        composeTestRule.waitFor(500)

        composeTestRule
            .onNodeWithText("Ок")
            .assertIsDisplayed()
        Log.e("test assert", "Ок is displayed")
        composeTestRule
            .onNodeWithText("Отмена")
            .assertIsDisplayed()
        Log.e("test assert", "Отмена is displayed")
        composeTestRule
            .onNodeWithText("Отмена")
            .performClick()
        Log.e("test click", "Отмена")
        composeTestRule.waitFor(500)

        composeTestRule
            .onNodeWithTag("trashButton")
            .assertIsDisplayed()
        Log.e("test assert", "trashButton is displayed")
        composeTestRule
            .onNodeWithTag("trashButton")
            .performClick()
        Log.e("test click", "trashButton")
        composeTestRule.waitFor(500)

        composeTestRule
            .onNodeWithText("Ок")
            .assertIsDisplayed()
        Log.e("test assert", "Ок is displayed")
        composeTestRule
            .onNodeWithText("Отмена")
            .assertIsDisplayed()
        Log.e("test assert", "Отмена is displayed")
        composeTestRule
            .onNodeWithText("Отмена")
            .performClick()
        Log.e("test click", "Отмена")
    }

    @Test
    fun cloudSongsTest() {
        (songBookAPIAdapter as? TestAPIAdapterImpl)?.apply {
            val list = search("", OrderBy.BY_ID_DESC)

            val titleList = list.map { "${it.artist} - ${it.title}" }
            Log.e("test list", titleList.toString())

            composeTestRule
                .onNodeWithTag("drawerButtonMain")
                .performClick()
            Log.e("test click", "drawerButtonMain")
            composeTestRule.waitFor(500)
            composeTestRule
                .onNodeWithText(ARTIST_CLOUD_SONGS)
                .assertIsDisplayed()
            Log.e("test assert", "$ARTIST_CLOUD_SONGS is displayed")
            composeTestRule
                .onNodeWithText(ARTIST_CLOUD_SONGS)
                .performClick()
            Log.e("test click", ARTIST_CLOUD_SONGS)
            composeTestRule.waitFor(500)
            composeTestRule
                .onNodeWithText("Последние добавленные")
                .assertIsDisplayed()
            Log.e("test assert", "Последние добавленные is displayed")
            composeTestRule
                .onNodeWithText(list[2].visibleTitleWithRating)
                .assertIsDisplayed()
            Log.e("test assert", "${list[2].visibleTitleWithRating} is displayed")
            composeTestRule
                .onNodeWithText(list[2].visibleTitleWithRating)
                .performClick()
            Log.e("test click", list[2].visibleTitleWithRating)
            composeTestRule.waitFor(500)
            composeTestRule
                .onNodeWithText(list[2].visibleTitleWithArtistAndRating)
                .assertIsDisplayed()
            Log.e("test assert", "${list[2].visibleTitleWithArtistAndRating} is displayed")

        }
    }
}

fun AndroidComposeTestRule<out TestRule, out ComponentActivity>.waitFor(time: Long) {
    try {
        waitUntil(time) { false }
    } catch (e: ComposeTimeoutException) {
        Log.e("test wait", "timeout")
    }
}