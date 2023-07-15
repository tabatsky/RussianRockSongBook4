package jatx.russianrocksongbook

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import jatx.russianrocksongbook.domain.repository.cloud.CloudRepository
import jatx.russianrocksongbook.domain.repository.cloud.OrderBy
import jatx.russianrocksongbook.domain.repository.local.*
import jatx.russianrocksongbook.domain.repository.preferences.ARTIST_KINO
import jatx.russianrocksongbook.domain.repository.preferences.Orientation
import jatx.russianrocksongbook.domain.repository.preferences.SettingsRepository
import jatx.russianrocksongbook.donation.api.view.donationLabel
import jatx.russianrocksongbook.donationhelper.api.DONATIONS
import jatx.russianrocksongbook.localsongs.api.methods.parseAndExecuteVoiceCommand
import jatx.russianrocksongbook.localsongs.api.methods.selectArtist
import jatx.russianrocksongbook.localsongs.api.methods.selectSongByArtistAndTitle
import jatx.russianrocksongbook.navigation.ScreenVariant
import jatx.russianrocksongbook.viewmodel.deps.impl.ToastsTestImpl
import jatx.russianrocksongbook.testing.*
import jatx.russianrocksongbook.viewmodel.CommonViewModel
import leakcanary.DetectLeaksAfterTestSuccess
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
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
const val ARTIST_3 = "ДДТ"

const val TITLE_1_1 = "Santa Maria"
const val TITLE_1_2 = "Яблочный остров"
const val TITLE_1_3 = "Над мертвым городом сон"
const val TITLE_1_4 = "Atlantica"
const val TITLE_2_1 = "17 лет"
const val TITLE_2_2 = "Поплачь о нем"
const val TITLE_3_1 = "Белая ночь"

const val ARTIST_NEW = "Новый исполнитель"
const val TITLE_NEW = "Новая песня"
val TEXT_NEW = """
    Какой-то
    Текст песни
    С какими-то
    Аккордами
""".trimIndent()

const val WARNING_COMMENT = "Комментарий"

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
class UITest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val rule = DetectLeaksAfterTestSuccess()

    @Inject
    lateinit var localRepository: LocalRepository

    @Inject
    lateinit var cloudRepository: CloudRepository

    @Inject
    lateinit var settingsRepository: SettingsRepository

    @Inject
    lateinit var stringConst: StringConst

    @Before
    fun init() {
        TestingConfig.isTesting = true

        hiltRule.inject()

        settingsRepository.orientation = Orientation.RANDOM
        settingsRepository.defaultArtist = ARTIST_KINO

        composeTestRule.activityRule.scenario.recreate()
        composeTestRule.waitFor(timeout)

        val appWasUpdated = settingsRepository.appWasUpdated

        while (settingsRepository.appWasUpdated) {
            composeTestRule.waitFor(1000L)
        }

        if (appWasUpdated) {
            composeTestRule
                .onNodeWithText(stringConst.ok)
                .performClick()
            Log.e("before click", stringConst.ok)
            composeTestRule.waitFor(timeout)
        }
    }

    @After
    fun clean() {
        composeTestRule.activityRule.scenario.onActivity {
            it.clean()
        }
        composeTestRule.activityRule.scenario.close()
    }

    @Test
    fun test0101_menuIsOpeningAndClosingWithDrawerButtonCorrectly() {
        val testNumber = 101

        composeTestRule.waitFor(timeout)

        composeTestRule
            .onNodeWithTag(DRAWER_BUTTON_MAIN)
            .performClick()
        Log.e("test $testNumber click", DRAWER_BUTTON_MAIN)
        composeTestRule.waitFor(timeout)
        composeTestRule
            .onNodeWithText(stringConst.menu)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${stringConst.menu} is displayed")

        composeTestRule
            .onNodeWithTag(DRAWER_BUTTON_MAIN)
            .performClick()
        Log.e("test $testNumber click", DRAWER_BUTTON_MAIN)
        composeTestRule.waitFor(timeout)
        composeTestRule
            .onNodeWithText(settingsRepository.defaultArtist)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${settingsRepository.defaultArtist} is displayed")

    }

    @Test
    fun test0102_menuPredefinedArtistsAreDisplayingCorrectly() {
        val testNumber = 102

        composeTestRule.waitFor(timeout)

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
    }

    @Test
    fun test0103_menuIsScrollingCorrectly() {
        val testNumber = 103

        val artists = localRepository.getArtistsAsList()

        composeTestRule.waitFor(timeout)

        composeTestRule
            .onNodeWithTag(DRAWER_BUTTON_MAIN)
            .performClick()
        Log.e("test $testNumber click", DRAWER_BUTTON_MAIN)

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
    }

    @Test
    fun test0104_menuArtistIsOpeningCorrectly() {
        val testNumber = 104

        val artists = localRepository.getArtistsAsList()

        composeTestRule.waitFor(timeout)

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
        composeTestRule.waitFor(timeout)

        val songs = localRepository.getSongsByArtistAsList(ARTIST_1)

        composeTestRule
            .onNodeWithText(songs[0].title)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${songs[0].title} is displayed")
        composeTestRule
            .onNodeWithText(songs[1].title)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${songs[1].title} is displayed")
        composeTestRule
            .onNodeWithText(songs[2].title)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${songs[2].title} is displayed")
    }

    @Test
    fun test0105_songListIsScrollingCorrectly() {
        val testNumber = 105

        composeTestRule.waitFor(timeout)

        val titles = localRepository
            .getSongsByArtistAsList(ARTIST_1)
            .map { it.title }

        composeTestRule.activityRule.scenario.onActivity {
            selectArtist(ARTIST_1)
        }

        composeTestRule.waitFor(timeout)

        composeTestRule
            .onNodeWithText(TITLE_1_1)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$TITLE_1_1 is displayed")

        composeTestRule
            .onNodeWithText(TITLE_1_2)
            .assertDoesNotExist()
        Log.e("test $testNumber assert", "$TITLE_1_2 does not exist")
        val indexSong2 = (titles.indexOf(TITLE_1_2) - 3)
            .takeIf { it >= 0 }
            ?: 0
        composeTestRule
            .onNodeWithTag(SONG_LIST_LAZY_COLUMN)
            .performScrollToIndex(indexSong2)
        Log.e("test $testNumber scroll", "songList to index $indexSong2")
        composeTestRule.waitFor(timeout)
        composeTestRule
            .onNodeWithText(TITLE_1_2)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$TITLE_1_2 is displayed")

        composeTestRule
            .onNodeWithText(TITLE_1_3)
            .assertDoesNotExist()
        Log.e("test $testNumber assert", "$TITLE_1_3 does not exist")
        val indexSong3 = (titles.indexOf(TITLE_1_3) - 3)
            .takeIf { it >= 0 }
            ?: 0
        composeTestRule
            .onNodeWithTag(SONG_LIST_LAZY_COLUMN)
            .performScrollToIndex(indexSong3)
        Log.e("test $testNumber scroll", "songList to index $indexSong3")
        composeTestRule.waitFor(timeout)
        composeTestRule
            .onNodeWithText(TITLE_1_3)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$TITLE_1_3 is displayed")

        composeTestRule
            .onNodeWithText(TITLE_1_4)
            .assertDoesNotExist()
        Log.e("test $testNumber assert", "$TITLE_1_4 does not exist")
        val indexSong4 = (titles.indexOf(TITLE_1_4) - 3)
            .takeIf { it >= 0 }
            ?: 0
        composeTestRule
            .onNodeWithTag(SONG_LIST_LAZY_COLUMN)
            .performScrollToIndex(indexSong4)
        Log.e("test $testNumber scroll", "songList to index $indexSong4")
        composeTestRule.waitFor(timeout)
        composeTestRule
            .onNodeWithText(TITLE_1_4)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$TITLE_1_4 is displayed")
    }

    @Test
    fun test0201_songTextIsOpeningCorrectly() {
        val testNumber = 201

        composeTestRule.waitFor(timeout)

        composeTestRule.activityRule.scenario.onActivity {
            selectArtist(ARTIST_1)
        }

        composeTestRule.waitFor(timeout)

        val songs = localRepository
            .getSongsByArtistAsList(ARTIST_1)
        val titles = songs
            .map { it.title }
        val songIndex1 = titles.indexOf(TITLE_1_3)
        val song1 = songs[songIndex1]

        composeTestRule
            .onNodeWithTag(SONG_LIST_LAZY_COLUMN)
            .performScrollToIndex(songIndex1 - 3)
        Log.e("test $testNumber scroll", "songList to index ${songIndex1 - 3}")
        composeTestRule.waitFor(timeout)

        composeTestRule
            .onNodeWithText(TITLE_1_3)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$TITLE_1_3 is displayed")
        composeTestRule
            .onNodeWithText(TITLE_1_3)
            .performClick()
        composeTestRule.waitFor(timeout)
        Log.e("test $testNumber click", TITLE_1_3)

        composeTestRule
            .onNodeWithText("${song1.title} (${song1.artist})")
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "song title with artist is displayed")
        composeTestRule
            .onNodeWithTag(SONG_TEXT_VIEWER)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$SONG_TEXT_VIEWER is displayed")
        Espresso.onView(withText(song1.text)).check(matches(isDisplayed()))
        Log.e("test $testNumber assert", "song text is displayed")
    }

    @Test
    fun test0202_songTextEditorIsOpeningAndClosingCorrectly() {
        val testNumber = 202

        composeTestRule.waitFor(timeout)

        composeTestRule.activityRule.scenario.onActivity {
            selectSongByArtistAndTitle(ARTIST_1, TITLE_1_3)
        }

        composeTestRule.waitFor(timeout)

        val songs = localRepository
            .getSongsByArtistAsList(ARTIST_1)
        val titles = songs
            .map { it.title }
        val songIndex1 = titles.indexOf(TITLE_1_3)
        val song1 = songs[songIndex1]

        composeTestRule
            .onNodeWithText("${song1.title} (${song1.artist})")
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "song title with artist is displayed")
        composeTestRule
            .onNodeWithTag(SONG_TEXT_VIEWER)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$SONG_TEXT_VIEWER is displayed")
        Espresso.onView(withText(song1.text)).check(matches(isDisplayed()))
        Log.e("test $testNumber assert", "song text is displayed")
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
        Espresso.onView(withText(song1.text)).check(matches(isDisplayed()))
        Log.e("test $testNumber assert", "song text is displayed")
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
    }

    @Test
    fun test0203_songTextMusicButtonsCountIsEqualsTwo() {
        val testNumber = 203

        composeTestRule.waitFor(timeout)

        composeTestRule.activityRule.scenario.onActivity {
            selectSongByArtistAndTitle(ARTIST_1, TITLE_1_3)
        }

        composeTestRule.waitFor(timeout)

        composeTestRule
            .onAllNodesWithTag(MUSIC_BUTTON)
            .assertCountEquals(2)
        Log.e("test $testNumber assert", "$MUSIC_BUTTON count is 2")
    }

    @Test
    fun test0204_songTextLeftAndRightButtonsAreWorkingCorrectly() {
        val testNumber = 204

        composeTestRule.waitFor(timeout)

        composeTestRule.activityRule.scenario.onActivity {
            selectSongByArtistAndTitle(ARTIST_1, TITLE_1_3)
        }

        composeTestRule.waitFor(timeout)

        val songs = localRepository
            .getSongsByArtistAsList(ARTIST_1)
        val titles = songs
            .map { it.title }
        val songIndex1 = titles.indexOf(TITLE_1_3)
        val song1 = songs[songIndex1]

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
        Espresso.onView(withText(song2.text)).check(matches(isDisplayed()))
        Log.e("test $testNumber assert", "song text is displayed")

        composeTestRule
            .onNodeWithTag(LEFT_BUTTON)
            .performClick()
        Log.e("test $testNumber click", LEFT_BUTTON)
        composeTestRule.waitFor(timeout)
        composeTestRule
            .onNodeWithText("${song1.title} (${song1.artist})")
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "song1 title with artist is displayed")
    }

    @Test
    fun test0205_songTextAddingAndRemovingFromFavoriteIsWorkingCorrectly() {
        val testNumber = 205

        composeTestRule.waitFor(timeout)

        localRepository.setFavorite(false, ARTIST_1, TITLE_1_3)

        composeTestRule.activityRule.scenario.onActivity {
            selectSongByArtistAndTitle(ARTIST_1, TITLE_1_3)
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

        assertTrue(ToastsTestImpl.verifyText(stringConst.toastAddedToFavorite))
        assertTrue(ToastsTestImpl.verifyText(stringConst.toastRemovedFromFavorite))
        Log.e("test $testNumber toast", "toasts shown")
    }

    @Test
    fun test0206_songTextUploadButtonOutOfTheBoxIsWorkingCorrectly() {
        val testNumber = 206

        composeTestRule.waitFor(timeout)

        composeTestRule.activityRule.scenario.onActivity {
            selectSongByArtistAndTitle(ARTIST_1, TITLE_1_3)
        }

        composeTestRule.waitFor(timeout)

        composeTestRule
            .onNodeWithTag(UPLOAD_BUTTON)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$UPLOAD_BUTTON is displayed")
        composeTestRule
            .onNodeWithTag(UPLOAD_BUTTON)
            .performClick()
        Log.e("test $testNumber click", UPLOAD_BUTTON)

        assertTrue(ToastsTestImpl.verifyText(stringConst.toastSongIsOutOfTheBox))
        Log.e("test $testNumber toast", "toasts shown")
    }

    @Test
    fun test0207_songTextUploadButtonNotOutOfTheBoxIsWorkingCorrectly() {
        val testNumber = 207

        var song = localRepository.getSongByArtistAndTitle(ARTIST_1, TITLE_1_3)!!
        val origText = song.text
        song = song.copy(text = "dsgssdg sdg fdg")
        localRepository.updateSong(song)

        composeTestRule.waitFor(timeout)

        song = localRepository.getSongByArtistAndTitle(ARTIST_1, TITLE_1_3)!!
        assertEquals(song.outOfTheBox, false)

        composeTestRule.activityRule.scenario.onActivity {
            selectSongByArtistAndTitle(ARTIST_1, TITLE_1_3)
        }

        composeTestRule.waitFor(timeout)

        composeTestRule
            .onNodeWithTag(UPLOAD_BUTTON)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$UPLOAD_BUTTON is displayed")
        composeTestRule
            .onNodeWithTag(UPLOAD_BUTTON)
            .performClick()
        Log.e("test $testNumber click", UPLOAD_BUTTON)

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

        song = song.copy(text = origText)
        localRepository.updateSong(song)

        composeTestRule.waitFor(timeout)

        song = localRepository.getSongByArtistAndTitle(ARTIST_1, TITLE_1_3)!!
        assertEquals(song.outOfTheBox, true)

        assertFalse(ToastsTestImpl.verifyText(stringConst.toastSongIsOutOfTheBox))
        Log.e("test $testNumber toast", "toasts shown")
    }

    @Test
    fun test0208_songTextWarningDialogIsWorkingCorrectly() {
        val testNumber = 208

        composeTestRule.waitFor(timeout)

        composeTestRule.activityRule.scenario.onActivity {
            selectSongByArtistAndTitle(ARTIST_1, TITLE_1_3)
        }

        composeTestRule.waitFor(timeout)

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
            .onNodeWithText(stringConst.ok)
            .performClick()
        Log.e("test $testNumber click", stringConst.ok)
        composeTestRule
            .onNodeWithText(stringConst.cancel)
            .performClick()
        Log.e("test $testNumber click", stringConst.cancel)
        composeTestRule.waitFor(timeout)

        composeTestRule
            .onNodeWithTag(WARNING_BUTTON)
            .performClick()
        Log.e("test $testNumber click", WARNING_BUTTON)
        composeTestRule.waitFor(timeout)
        composeTestRule
            .onNodeWithTag(TEXT_FIELD_WARNING_COMMENT)
            .performTextReplacement(WARNING_COMMENT)
        composeTestRule
            .onNodeWithText(stringConst.ok)
            .performClick()
        Log.e("test $testNumber click", stringConst.ok)
        composeTestRule.waitFor(timeout)

        assertTrue(ToastsTestImpl.verifyText(stringConst.toastCommentCannotBeEmpty))
        assertTrue(ToastsTestImpl.verifyText(stringConst.toastSendWarningSuccess))
        Log.e("test $testNumber toast", "toasts shown")
    }

    @Test
    fun test0209_songTextSongToTrashDialogIsOpeningCorrectly() {
        val testNumber = 209

        composeTestRule.waitFor(timeout)

        composeTestRule.activityRule.scenario.onActivity {
            selectSongByArtistAndTitle(ARTIST_1, TITLE_1_3)
        }

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

        composeTestRule.waitFor(timeout)
    }

    @Test
    fun test0301_cloudSearchIsOpeningFromMenuCorrectly() {
        val testNumber = 301

        composeTestRule.waitFor(timeout)

        with (cloudRepository) {
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
                .onNodeWithText(OrderBy.BY_ID_DESC.orderByRus)
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "${OrderBy.BY_ID_DESC.orderByRus} is displayed")
            composeTestRule
                .onNodeWithText(list[2].visibleTitleWithRating)
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "${list[2].visibleTitleWithRating} is displayed")
        }
    }

    @Test
    fun test0302_cloudSearchOfflineIsWorkingCorrectly() {
        val testNumber = 302

        composeTestRule.waitFor(timeout)

        with (cloudRepository) {
            composeTestRule.activityRule.scenario.onActivity {
                CommonViewModel
                    .getStoredInstance()
                    ?.selectScreen(ScreenVariant.CloudSearch())
            }

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
        }
    }

    @Test
    fun test0303_cloudSearchNormalQueryIsWorkingCorrectly() {
        val testNumber = 303

        composeTestRule.waitFor(timeout)

        with (cloudRepository) {
            composeTestRule.activityRule.scenario.onActivity {
                CommonViewModel
                    .getStoredInstance()
                    ?.selectScreen(ScreenVariant.CloudSearch())
            }

            composeTestRule
                .onNodeWithTag(TEXT_FIELD_SEARCH_FOR)
                .performTextReplacement("Ло")
            Log.e("test $testNumber input", "Ло")
            composeTestRule
                .onNodeWithTag(SEARCH_BUTTON)
                .performClick()
            Log.e("test $testNumber click", SEARCH_BUTTON)
            composeTestRule.waitFor(timeout)

            val list = search("Ло", OrderBy.BY_ID_DESC)
            val titleList = list.map { "${it.artist} - ${it.title}" }
            Log.e("test $testNumber list", titleList.toString())

            composeTestRule
                .onNodeWithText(list[2].visibleTitleWithRating)
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "${list[2].visibleTitleWithRating} is displayed")
        }
    }

    @Test
    fun test0304_cloudSearchOrderBySpinnerValuesAreDisplayingCorrectly() {
        val testNumber = 304

        composeTestRule.waitFor(timeout)

        composeTestRule.activityRule.scenario.onActivity {
            CommonViewModel
                .getStoredInstance()
                ?.selectScreen(ScreenVariant.CloudSearch())
        }

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
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${OrderBy.BY_TITLE.orderByRus} is displayed")
        composeTestRule.waitFor(timeout)
    }

    @Test
    fun test0305_cloudSearchOrderingByTitleIsWorkingCorrectly() {
        val testNumber = 305

        composeTestRule.waitFor(timeout)

        with (cloudRepository) {
            composeTestRule.activityRule.scenario.onActivity {
                CommonViewModel
                    .getStoredInstance()
                    ?.selectScreen(ScreenVariant.CloudSearch())
            }

            composeTestRule
                .onNodeWithTag(TEXT_FIELD_SEARCH_FOR)
                .performTextReplacement("Ло")
            Log.e("test $testNumber input", "Ло")
            composeTestRule
                .onNodeWithTag(SEARCH_BUTTON)
                .performClick()
            Log.e("test $testNumber click", SEARCH_BUTTON)
            composeTestRule.waitFor(timeout)

            composeTestRule
                .onNodeWithText(OrderBy.BY_ID_DESC.orderByRus)
                .performClick()
            Log.e("test $testNumber click", OrderBy.BY_ID_DESC.orderByRus)
            composeTestRule.waitFor(timeout)
            composeTestRule
                .onNodeWithText(OrderBy.BY_TITLE.orderByRus)
                .performClick()
            Log.e("test $testNumber click", OrderBy.BY_TITLE.orderByRus)
            composeTestRule.waitFor(timeout)

            val list = search("Ло", OrderBy.BY_TITLE)
            val titleList = list.map { "${it.artist} - ${it.title}" }
            Log.e("test $testNumber list", titleList.toString())

            composeTestRule
                .onNodeWithText(list[2].visibleTitleWithRating)
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "${list[2].visibleTitleWithRating} is displayed")
        }
    }

    @Test
    fun test0306_cloudSearchOrderingByArtistIsWorkingCorrectly() {
        val testNumber = 306

        composeTestRule.waitFor(timeout)

        with (cloudRepository) {
            composeTestRule.activityRule.scenario.onActivity {
                CommonViewModel
                    .getStoredInstance()
                    ?.selectScreen(ScreenVariant.CloudSearch())
            }

            composeTestRule
                .onNodeWithTag(TEXT_FIELD_SEARCH_FOR)
                .performTextReplacement("Ло")
            Log.e("test $testNumber input", "Ло")
            composeTestRule
                .onNodeWithTag(SEARCH_BUTTON)
                .performClick()
            Log.e("test $testNumber click", SEARCH_BUTTON)
            composeTestRule.waitFor(timeout)

            composeTestRule
                .onNodeWithText(OrderBy.BY_ID_DESC.orderByRus)
                .performClick()
            Log.e("test $testNumber click", OrderBy.BY_ID_DESC.orderByRus)
            composeTestRule.waitFor(timeout)
            composeTestRule
                .onNodeWithText(OrderBy.BY_ARTIST.orderByRus)
                .performClick()
            Log.e("test $testNumber click", OrderBy.BY_ARTIST.orderByRus)
            composeTestRule.waitFor(timeout)

            val list = search("Ло", OrderBy.BY_ARTIST)
            val titleList = list.map { "${it.artist} - ${it.title}" }
            Log.e("test $testNumber list", titleList.toString())

            composeTestRule
                .onNodeWithText(list[2].visibleTitleWithRating)
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "${list[2].visibleTitleWithRating} is displayed")
        }
    }

    @Test
    fun test0307_cloudSearchQueryWithEmptyResultIsWorkingCorrectly() {
        val testNumber = 307

        composeTestRule.waitFor(timeout)

        composeTestRule.activityRule.scenario.onActivity {
            CommonViewModel
                .getStoredInstance()
                ?.selectScreen(ScreenVariant.CloudSearch())
        }

        composeTestRule.waitFor(timeout)

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

    @Test
    fun test0401_cloudSongTextIsOpeningFromCloudSearchCorrectly() {
        val testNumber = 401

        composeTestRule.waitFor(timeout)

        with (cloudRepository) {
            val list = search("", OrderBy.BY_ID_DESC)
            val titleList = list.map { "${it.artist} - ${it.title}" }
            Log.e("test $testNumber list", titleList.toString())

            composeTestRule.activityRule.scenario.onActivity {
                CommonViewModel
                    .getStoredInstance()
                    ?.selectScreen(ScreenVariant.CloudSearch())
            }

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
            Espresso.onView(withText(list[2].text)).check(matches(isDisplayed()))
            Log.e("test $testNumber assert", "song text is displayed")
        }
    }

    @Test
    fun test0402_cloudSongTextAllButtonsAreDisplaying() {
        val testNumber = 402

        composeTestRule.waitFor(timeout)

        composeTestRule.activityRule.scenario.onActivity {
            CommonViewModel
                .getStoredInstance()
                ?.selectScreen(ScreenVariant.CloudSearch())
        }

        composeTestRule.waitFor(timeout)

        composeTestRule.activityRule.scenario.onActivity {
            CommonViewModel
                .getStoredInstance()
                ?.selectScreen(ScreenVariant.CloudSongText(2))
        }

        composeTestRule.waitFor(timeout)

        composeTestRule
            .onAllNodesWithTag(MUSIC_BUTTON)
            .assertCountEquals(2)
        Log.e("test $testNumber assert", "$MUSIC_BUTTON count is 2")

        composeTestRule
            .onNodeWithTag(DOWNLOAD_BUTTON)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$DOWNLOAD_BUTTON is displayed")

        composeTestRule
            .onNodeWithTag(WARNING_BUTTON)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$WARNING_BUTTON is displayed")

        composeTestRule
            .onNodeWithTag(LIKE_BUTTON)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$LIKE_BUTTON is displayed")

        composeTestRule
            .onNodeWithTag(DISLIKE_BUTTON)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$DISLIKE_BUTTON is displayed")

        composeTestRule
            .onNodeWithTag(BACK_BUTTON)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$BACK_BUTTON is displayed")

        composeTestRule
            .onNodeWithTag(LEFT_BUTTON)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$LEFT_BUTTON is displayed")

        composeTestRule
            .onNodeWithTag(RIGHT_BUTTON)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$RIGHT_BUTTON is displayed")
    }

    @Test
    fun test0403_cloudSongTextLeftAndRightButtonsAreWorkingCorrectly() {
        val testNumber = 403

        composeTestRule.waitFor(timeout)

        with (cloudRepository) {
            val list = search("", OrderBy.BY_ID_DESC)
            val titleList = list.map { "${it.artist} - ${it.title}" }
            Log.e("test $testNumber list", titleList.toString())

            composeTestRule.activityRule.scenario.onActivity {
                CommonViewModel
                    .getStoredInstance()
                    ?.selectScreen(ScreenVariant.CloudSearch())
            }

            composeTestRule.waitFor(timeout)

            composeTestRule.activityRule.scenario.onActivity {
                CommonViewModel
                    .getStoredInstance()
                    ?.selectScreen(ScreenVariant.CloudSongText(2))
            }

            composeTestRule.waitFor(timeout)

            composeTestRule
                .onNodeWithText(list[2].visibleTitleWithArtistAndRating)
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "${list[2].visibleTitleWithArtistAndRating} is displayed")
            Espresso.onView(withText(list[2].text)).check(matches(isDisplayed()))
            Log.e("test $testNumber assert", "song text is displayed")
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
            Espresso.onView(withText(list[3].text)).check(matches(isDisplayed()))
            Log.e("test $testNumber assert", "song text is displayed")
            composeTestRule
                .onNodeWithTag(NUMBER_LABEL)
                .assertTextContains("4 /", substring = true)
            Log.e("test $testNumber assert", "$NUMBER_LABEL contains '4 /'")

            composeTestRule
                .onNodeWithTag(LEFT_BUTTON)
                .performClick()
            Log.e("test $testNumber click", LEFT_BUTTON)
            composeTestRule.waitFor(timeout)

            composeTestRule
                .onNodeWithText(list[2].visibleTitleWithArtistAndRating)
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "${list[2].visibleTitleWithArtistAndRating} is displayed")
            Espresso.onView(withText(list[2].text)).check(matches(isDisplayed()))
            Log.e("test $testNumber assert", "song text is displayed")
            composeTestRule
                .onNodeWithTag(NUMBER_LABEL)
                .assertTextContains("3 /", substring = true)
            Log.e("test $testNumber assert", "$NUMBER_LABEL contains '3 /'")
        }
    }

    @Test
    fun test0404_cloudSongTextWarningDialogIsWorkingCorrectly() {
        val testNumber = 404

        composeTestRule.waitFor(timeout)

        composeTestRule.activityRule.scenario.onActivity {
            CommonViewModel
                .getStoredInstance()
                ?.selectScreen(ScreenVariant.CloudSearch())
        }

        composeTestRule.waitFor(timeout)

        composeTestRule.activityRule.scenario.onActivity {
            CommonViewModel
                .getStoredInstance()
                ?.selectScreen(ScreenVariant.CloudSongText(2))
        }

        composeTestRule.waitFor(timeout)

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

        composeTestRule
            .onNodeWithText(stringConst.sendWarningText)
            .assertDoesNotExist()
        Log.e("test $testNumber assert", "${stringConst.sendWarningText} does not exist")
        composeTestRule
            .onNodeWithText(stringConst.ok)
            .assertDoesNotExist()
        Log.e("test $testNumber assert", "${stringConst.ok} does not exist")
        composeTestRule
            .onNodeWithText(stringConst.cancel)
            .assertDoesNotExist()
        Log.e("test $testNumber assert", "${stringConst.cancel} does not exist")
    }

    @Test
    fun test0405_cloudSongTextDownloadButtonIsWorkingCorrectly() {
        val testNumber = 405

        composeTestRule.waitFor(timeout)

        with (cloudRepository) {
            val list = search("", OrderBy.BY_ID_DESC)
            val titleList = list.map { "${it.artist} - ${it.title}" }
            Log.e("test $testNumber list", titleList.toString())

            composeTestRule.activityRule.scenario.onActivity {
                CommonViewModel
                    .getStoredInstance()
                    ?.selectScreen(ScreenVariant.CloudSearch())
            }

            composeTestRule.waitFor(timeout)

            composeTestRule.activityRule.scenario.onActivity {
                CommonViewModel
                    .getStoredInstance()
                    ?.selectScreen(ScreenVariant.CloudSongText(2))
            }

            composeTestRule.waitFor(timeout)

            composeTestRule
                .onNodeWithTag(DOWNLOAD_BUTTON)
                .performClick()
            Log.e("test $testNumber click", DOWNLOAD_BUTTON)
            composeTestRule.waitFor(timeout)
            assert(
                localRepository
                    .getSongsByArtistAsList(list[2].artist)
                    .map { it.title }
                    .contains(list[2].visibleTitle)
            )
            Log.e("test $testNumber assert", "${list[2].artist} contains ${list[2].visibleTitle}")
            assert(
                localRepository
                    .getSongsByArtistAsList(ARTIST_FAVORITE)
                    .map { it.title }
                    .contains(list[2].visibleTitle)
            )
            Log.e("test $testNumber assert", "$ARTIST_FAVORITE contains ${list[2].visibleTitle}")

            assertTrue(ToastsTestImpl.verifyText(stringConst.toastChordsSavedAndAddedToFavorite))
            Log.e("test $testNumber toast", "toasts shown")
        }
    }

    @Test
    fun test0406_cloudSongTextAndCloudSearchLikeIsWorkingCorrectly() {
        val testNumber = 406

        composeTestRule.waitFor(timeout)

        with (cloudRepository) {
            val list = search("", OrderBy.BY_ID_DESC)
            val titleList = list.map { "${it.artist} - ${it.title}" }
            Log.e("test $testNumber list", titleList.toString())

            composeTestRule.activityRule.scenario.onActivity {
                CommonViewModel
                    .getStoredInstance()
                    ?.selectScreen(ScreenVariant.CloudSearch())
            }

            composeTestRule.waitFor(timeout)

            composeTestRule.activityRule.scenario.onActivity {
                CommonViewModel
                    .getStoredInstance()
                    ?.selectScreen(ScreenVariant.CloudSongText(2))
            }

            composeTestRule.waitFor(timeout)

            val cloudSong2 = list[2].let {
                it.copy(likeCount = it.likeCount + 1)
            }

            composeTestRule
                .onNodeWithTag(LIKE_BUTTON)
                .performClick()
            Log.e("test $testNumber click", LIKE_BUTTON)
            composeTestRule.waitFor(timeout)
            composeTestRule
                .onNodeWithText(cloudSong2.visibleTitleWithArtistAndRating)
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "${cloudSong2.visibleTitleWithArtistAndRating} is displayed")
            assert(cloudSong2.likeCount == 1)
            Log.e("test $testNumber assert", "likeCount == 1")

            composeTestRule
                .onNodeWithTag(BACK_BUTTON)
                .performClick()
            Log.e("test $testNumber click", BACK_BUTTON)
            composeTestRule.waitFor(timeout)

            composeTestRule
                .onNodeWithText(cloudSong2.visibleTitleWithRating)
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "${cloudSong2.visibleTitleWithRating} is displayed")

            assertTrue(ToastsTestImpl.verifyText(stringConst.toastVoteSuccess))
            Log.e("test $testNumber toast", "toasts shown")
        }
    }

    @Test
    fun test0407_cloudSongTextAndCloudSearchDislikeIsWorkingCorrectly() {
        val testNumber = 407

        composeTestRule.waitFor(timeout)

        with (cloudRepository) {
            val list = search("", OrderBy.BY_ID_DESC)
            val titleList = list.map { "${it.artist} - ${it.title}" }
            Log.e("test $testNumber list", titleList.toString())

            composeTestRule.activityRule.scenario.onActivity {
                CommonViewModel
                    .getStoredInstance()
                    ?.selectScreen(ScreenVariant.CloudSearch())
            }

            composeTestRule.waitFor(timeout)

            composeTestRule.activityRule.scenario.onActivity {
                CommonViewModel
                    .getStoredInstance()
                    ?.selectScreen(ScreenVariant.CloudSongText(3))
            }

            composeTestRule.waitFor(timeout)

            val cloudSong3 = list[3].let {
                it.copy(dislikeCount = it.dislikeCount + 1)
            }

            composeTestRule
                .onNodeWithTag(DISLIKE_BUTTON)
                .performClick()
            Log.e("test $testNumber click", DISLIKE_BUTTON)
            composeTestRule.waitFor(timeout)
            composeTestRule
                .onNodeWithText(cloudSong3.visibleTitleWithArtistAndRating)
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "${cloudSong3.visibleTitleWithArtistAndRating} is displayed")
            assert(cloudSong3.dislikeCount == 1)
            Log.e("test $testNumber assert", "dislikeCount == 1")

            composeTestRule
                .onNodeWithTag(BACK_BUTTON)
                .performClick()
            Log.e("test $testNumber click", BACK_BUTTON)
            composeTestRule.waitFor(timeout)

            composeTestRule
                .onNodeWithText(cloudSong3.visibleTitleWithRating)
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "${cloudSong3.visibleTitleWithRating} is displayed")

            assertTrue(ToastsTestImpl.verifyText(stringConst.toastVoteSuccess))
            Log.e("test $testNumber toast", "toasts shown")
        }
    }

    @Test
    fun test0500_addSongWithUploadingToCloudAndDeleting() {
        val testNumber = 500

        composeTestRule.waitFor(timeout)

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
            .onNodeWithText(stringConst.ok)
            .performClick()
        Log.e("test $testNumber click", stringConst.ok)
        composeTestRule.waitFor(timeout)

        with (cloudRepository) {
            val list = search("", OrderBy.BY_ID_DESC)
            val cloudSong = list[0]

            assert(cloudSong.artist == ARTIST_NEW)
            Log.e("test $testNumber assert", "new artist matches")
            assert(cloudSong.title == TITLE_NEW)
            Log.e("test $testNumber assert", "new title matches")
            assert(cloudSong.text == TEXT_NEW)
            Log.e("test $testNumber assert", "new text matches")
        }

        composeTestRule
            .onNodeWithText("$TITLE_NEW ($ARTIST_NEW)")
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "'Title (Artist)' is displayed")
        composeTestRule
            .onNodeWithTag(SONG_TEXT_VIEWER)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$SONG_TEXT_VIEWER is displayed")
        Espresso.onView(withText(TEXT_NEW)).check(matches(isDisplayed()))
        Log.e("test $testNumber assert", "song text is displayed")

        val song = localRepository.getSongByArtistAndTitle(ARTIST_NEW, TITLE_NEW)
        assert(song != null)
        assert(song!!.text == TEXT_NEW)
        Log.e("test $testNumber assert", "new song exists and text matches")

        composeTestRule
            .onNodeWithTag(TRASH_BUTTON)
            .performClick()
        Log.e("test $testNumber click", TRASH_BUTTON)
        composeTestRule.waitFor(timeout)

        composeTestRule
            .onNodeWithText(stringConst.ok)
            .performClick()
        Log.e("test $testNumber click", stringConst.ok)
        composeTestRule.waitFor(timeout)

        composeTestRule
            .onNodeWithText(stringConst.listIsEmpty)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${stringConst.listIsEmpty} is displayed")
        val artists = localRepository.getArtistsAsList()
        assert(!artists.contains(ARTIST_NEW))
        Log.e("test $testNumber assert", "new artist deleted")

        assertTrue(ToastsTestImpl.verifyText(stringConst.toastSongAdded))
        assertTrue(ToastsTestImpl.verifyText(stringConst.toastUploadToCloudSuccess))
        assertTrue(ToastsTestImpl.verifyText(stringConst.toastDeletedToTrash))
        Log.e("test $testNumber toast", "toasts shown")
    }

    @Test
    fun test0600_settings() {
        val testNumber = 600

        composeTestRule.waitFor(timeout)

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
    fun test0700_donation() {
        val testNumber = 700

        composeTestRule.waitFor(timeout)

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
    fun test0800_addArtist() {
        val testNumber = 800

        composeTestRule.waitFor(timeout)

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

    @Test
    fun test0900_favorite() {
        val testNumber = 900

        localRepository.setFavorite(true, ARTIST_1, TITLE_1_1)
        val song1 = localRepository.getSongByArtistAndTitle(ARTIST_1, TITLE_1_1)!!
        localRepository.setFavorite(true, ARTIST_2, TITLE_2_1)
        val song2 = localRepository.getSongByArtistAndTitle(ARTIST_2, TITLE_2_1)!!
        localRepository.setFavorite(true, ARTIST_1, TITLE_1_3)
        val song3 = localRepository.getSongByArtistAndTitle(ARTIST_1, TITLE_1_3)!!

        val songs = localRepository.getSongsByArtistAsList(ARTIST_FAVORITE)

        val index1 = songs.indexOf(song1)
        assertTrue(index1 >= 0)
        val index2 = songs.indexOf(song2)
        assertTrue(index1 >= 0)
        val index3 = songs.indexOf(song3)
        assertTrue(index1 >= 0)

        composeTestRule.waitFor(timeout)

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
            .onNodeWithText(ARTIST_FAVORITE)
            .performClick()
        Log.e("test $testNumber click", ARTIST_FAVORITE)
        composeTestRule.waitFor(timeout)

        composeTestRule
            .onNodeWithTag(SONG_LIST_LAZY_COLUMN)
            .performScrollToIndex(index1)
        Log.e("test $testNumber scroll", "songList to index $index1")
        composeTestRule.waitFor(timeout)
        composeTestRule
            .onNodeWithText(song1.title)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${song1.title} is displayed")
        composeTestRule.waitFor(timeout)

        composeTestRule
            .onNodeWithTag(SONG_LIST_LAZY_COLUMN)
            .performScrollToIndex(index2)
        Log.e("test $testNumber scroll", "songList to index $index2")
        composeTestRule.waitFor(timeout)
        composeTestRule
            .onNodeWithText(song2.title)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${song2.title} is displayed")
        composeTestRule.waitFor(timeout)

        composeTestRule
            .onNodeWithTag(SONG_LIST_LAZY_COLUMN)
            .performScrollToIndex(index3)
        Log.e("test $testNumber scroll", "songList to index $index3")
        composeTestRule.waitFor(timeout)
        composeTestRule
            .onNodeWithText(song3.title)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${song3.title} is displayed")
        composeTestRule.waitFor(timeout)

        localRepository.setFavorite(false, ARTIST_1, TITLE_1_3)

        composeTestRule.waitFor(timeout)
        composeTestRule
            .onNodeWithText(TITLE_1_3)
            .assertDoesNotExist()
        Log.e("test $testNumber assert", "$TITLE_1_3 does not exist")
        composeTestRule.waitFor(timeout)
    }

    @Test
    fun test1001_voiceCommandOpenArtistIsWorkingCorrectly() {
        val testNumber = 1001

        composeTestRule.activityRule.scenario.onActivity {
            parseAndExecuteVoiceCommand("открой группу $ARTIST_1")
        }

        composeTestRule.waitFor(timeout)

        composeTestRule
            .onNodeWithText(TITLE_1_4)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$TITLE_1_4 is displayed")
    }

    @Test
    fun test1002_voiceCommandOpenSongByTitleIsWorkingCorrectly() {
        val testNumber = 1002

        composeTestRule.activityRule.scenario.onActivity {
            parseAndExecuteVoiceCommand("открой песню $TITLE_1_1")
        }

        composeTestRule.waitFor(timeout)

        val song1 = localRepository.getSongByArtistAndTitle(ARTIST_1, TITLE_1_1)
        assertNotNull(song1)

        composeTestRule
            .onNodeWithText("$TITLE_1_1 ($ARTIST_1)")
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "song title with artist is displayed")
        composeTestRule
            .onNodeWithTag(SONG_TEXT_VIEWER)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$SONG_TEXT_VIEWER is displayed")
        Espresso.onView(withText(song1!!.text)).check(matches(isDisplayed()))
        Log.e("test $testNumber assert", "song text is displayed")
    }

    @Test
    fun test1003_voiceCommandOpenSongByArtistAndTitleIsWorkingCorrectly() {
        val testNumber = 1003

        composeTestRule.activityRule.scenario.onActivity {
            parseAndExecuteVoiceCommand("открой песню $ARTIST_3 $TITLE_3_1")
        }

        composeTestRule.waitFor(timeout)

        val song2 = localRepository.getSongByArtistAndTitle(ARTIST_3, TITLE_3_1)
        assertNotNull(song2)

        composeTestRule
            .onNodeWithText("$TITLE_3_1 ($ARTIST_3)")
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "song title with artist is displayed")
        composeTestRule
            .onNodeWithTag(SONG_TEXT_VIEWER)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$SONG_TEXT_VIEWER is displayed")
        Espresso.onView(withText(song2!!.text)).check(matches(isDisplayed()))
        Log.e("test $testNumber assert", "song text is displayed")
    }
}

const val timeout = 1500L

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
    val menu = context.getString(R.string.menu)
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
    val toastSongIsOutOfTheBox = context.getString(R.string.toast_song_is_out_of_the_box)
    val toastAddedToFavorite = context.getString(R.string.toast_added_to_favorite)
    val toastRemovedFromFavorite = context.getString(R.string.toast_removed_from_favorite)
    val toastCommentCannotBeEmpty = context.getString(R.string.toast_comment_cannot_be_empty)
    val toastSendWarningSuccess = context.getString(R.string.toast_send_warning_success)
    val toastChordsSavedAndAddedToFavorite =
        context.getString(R.string.toast_chords_saved_and_added_to_favorite)
    val toastVoteSuccess = context.getString(R.string.toast_vote_success)
    val toastSongAdded = context.getString(R.string.toast_song_added)
    val toastUploadToCloudSuccess = context.getString(R.string.toast_upload_to_cloud_success)
    val toastDeletedToTrash = context.getString(R.string.toast_deleted_to_trash)

    val themeList = context.resources.getStringArray(R.array.theme_list)
    val fontScaleList = context.resources.getStringArray(R.array.font_scale_list)
    val orientationList = context.resources.getStringArray(R.array.orientation_list)
    val listenToMusicVariants = context.resources.getStringArray(R.array.listen_to_music_variants)
}