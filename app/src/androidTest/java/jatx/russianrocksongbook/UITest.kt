package jatx.russianrocksongbook

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.mockk.core.ValueClassSupport.boxedValue
import jatx.russianrocksongbook.commonviewmodel.CommonViewModel
import jatx.russianrocksongbook.commonviewmodel.SelectScreen
import jatx.russianrocksongbook.commonviewmodel.deps.impl.ToastsTestImpl
import jatx.russianrocksongbook.domain.repository.cloud.CloudRepository
import jatx.russianrocksongbook.domain.repository.cloud.CloudSearchOrderBy
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
import jatx.russianrocksongbook.testing.*
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

        val appWasUpdated = settingsRepository.appWasUpdated

        while (settingsRepository.appWasUpdated) {
            composeTestRule.waitForTimeout(1000L)
        }

        if (appWasUpdated) {
            composeTestRule
                .onNodeWithText(stringConst.ok)
                .performClick()
            Log.e("before click", stringConst.ok)
            composeTestRule.waitForCondition {
                composeTestRule
                    .onNodeWithText(stringConst.ok)
                    .isNotDisplayed()
            }
        }
    }

    @After
    fun clean() {
        if (composeTestRule.activityRule.scenario.state == Lifecycle.State.DESTROYED) return
        composeTestRule.activityRule.scenario.onActivity {
            it.clean()
        }
        composeTestRule.activityRule.scenario.close()
    }

    @Test
    fun test0101_menuIsOpeningAndClosingWithDrawerButtonCorrectly() {
        val testNumber = 101

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(DRAWER_BUTTON_MAIN)
                .isDisplayed()
        }

        composeTestRule
            .onNodeWithTag(DRAWER_BUTTON_MAIN)
            .performClick()
        Log.e("test $testNumber click", DRAWER_BUTTON_MAIN)

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(stringConst.menu)
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithText(stringConst.menu)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${stringConst.menu} is displayed")

        composeTestRule
            .onNodeWithTag(DRAWER_BUTTON_MAIN)
            .performClick()
        Log.e("test $testNumber click", DRAWER_BUTTON_MAIN)
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(settingsRepository.defaultArtist)
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithText(settingsRepository.defaultArtist)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${settingsRepository.defaultArtist} is displayed")

    }

    @Test
    fun test0102_menuPredefinedArtistsAreDisplayingCorrectly() {
        val testNumber = 102

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(DRAWER_BUTTON_MAIN)
                .isDisplayed()
        }

        composeTestRule
            .onNodeWithTag(DRAWER_BUTTON_MAIN)
            .performClick()
        Log.e("test $testNumber click", DRAWER_BUTTON_MAIN)
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(ARTIST_FAVORITE)
                .isDisplayed()
        }
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

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(DRAWER_BUTTON_MAIN)
                .isDisplayed()
        }

        composeTestRule
            .onNodeWithTag(DRAWER_BUTTON_MAIN)
            .performClick()
        Log.e("test $testNumber click", DRAWER_BUTTON_MAIN)

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(MENU_LAZY_COLUMN)
                .isDisplayed()
        }
        val index1 = artists.predefinedArtistsWithGroups().indexOf(ARTIST_1.artistGroup()) - 3
        composeTestRule
            .onNodeWithTag(MENU_LAZY_COLUMN)
            .performScrollToIndex(index1)
        Log.e("test $testNumber scroll", "menu to index $index1")
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(ARTIST_1.artistGroup())
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithText(ARTIST_1.artistGroup())
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${ARTIST_1.artistGroup()} is displayed")

        val index2 = artists.predefinedArtistsWithGroups().indexOf(ARTIST_2.artistGroup()) - 3
        composeTestRule
            .onNodeWithTag(MENU_LAZY_COLUMN)
            .performScrollToIndex(index2)
        Log.e("test $testNumber scroll", "menu to index $index2")
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(ARTIST_2.artistGroup())
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithText(ARTIST_2.artistGroup())
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${ARTIST_2.artistGroup()} is displayed")
    }

    @Test
    fun test0104_songListForArtistIsOpeningFromMenuCorrectly() {
        val testNumber = 104

        val artists = localRepository.getArtistsAsList()

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(DRAWER_BUTTON_MAIN)
                .isDisplayed()
        }

        composeTestRule
            .onNodeWithTag(DRAWER_BUTTON_MAIN)
            .performClick()
        Log.e("test $testNumber click", DRAWER_BUTTON_MAIN)
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(MENU_LAZY_COLUMN)
                .isDisplayed()
        }

        val index1 = artists.predefinedArtistsWithGroups().indexOf(ARTIST_1.artistGroup()) - 3
        composeTestRule
            .onNodeWithTag(MENU_LAZY_COLUMN)
            .performScrollToIndex(index1)
        Log.e("test $testNumber scroll", "menu to index $index1")
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(ARTIST_1.artistGroup())
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithText(ARTIST_1.artistGroup())
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${ARTIST_1.artistGroup()} is displayed")
        composeTestRule
            .onNodeWithText(ARTIST_1.artistGroup())
            .performClick()

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(ARTIST_1)
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithText(ARTIST_1)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$ARTIST_1 is displayed")
        composeTestRule
            .onNodeWithText(ARTIST_1)
            .performClick()
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(APP_BAR_TITLE)
                .isDisplayed()
        }

        val actualArtist = composeTestRule
            .onNodeWithTag(APP_BAR_TITLE)
            .text ?: "error"

        assertTrue(actualArtist.startEquallyWith(ARTIST_1))
        Log.e("test $testNumber assert", "app bar title starts equally with $ARTIST_1")

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
    fun test0105_songListForArtist_BackActionAfterOpeningFromMenuIsWorkingCorrectly() {
        val testNumber = 105

        val artists = localRepository.getArtistsAsList()

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(DRAWER_BUTTON_MAIN)
                .isDisplayed()
        }

        composeTestRule
            .onNodeWithTag(DRAWER_BUTTON_MAIN)
            .performClick()
        Log.e("test $testNumber click", DRAWER_BUTTON_MAIN)
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(MENU_LAZY_COLUMN)
                .isDisplayed()
        }

        val index1 = artists.predefinedArtistsWithGroups().indexOf(ARTIST_1.artistGroup()) - 3
        composeTestRule
            .onNodeWithTag(MENU_LAZY_COLUMN)
            .performScrollToIndex(index1)
        Log.e("test $testNumber scroll", "menu to index $index1")
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(ARTIST_1.artistGroup())
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithText(ARTIST_1.artistGroup())
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${ARTIST_1.artistGroup()} is displayed")
        composeTestRule
            .onNodeWithText(ARTIST_1.artistGroup())
            .performClick()
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(ARTIST_1)
                .isDisplayed()
        }

        composeTestRule
            .onNodeWithText(ARTIST_1)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$ARTIST_1 is displayed")
        composeTestRule
            .onNodeWithText(ARTIST_1)
            .performClick()
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(APP_BAR_TITLE)
                .isDisplayed()
        }

        val actualArtist = composeTestRule
            .onNodeWithTag(APP_BAR_TITLE)
            .text ?: "error"

        assertTrue(actualArtist.startEquallyWith(ARTIST_1))
        Log.e("test $testNumber assert", "app bar title starts equally with $ARTIST_1")

        composeTestRule.pressBackUnconditionally()
        Log.e("test $testNumber action", "press back")

        composeTestRule.waitForCondition {
            composeTestRule.activityRule.scenario.state == Lifecycle.State.DESTROYED
        }

        assertEquals(composeTestRule.activityRule.scenario.state, Lifecycle.State.DESTROYED)
        Log.e("test $testNumber assert", "activity is destroyed")
    }

    @Test
    fun test0106_songListIsScrollingCorrectly() {
        val testNumber = 106

        val titles = localRepository
            .getSongsByArtistAsList(ARTIST_1)
            .map { it.title }

        composeTestRule.activityRule.scenario.onActivity {
            selectArtist(ARTIST_1)
        }

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(TITLE_1_1)
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithText(TITLE_1_1)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$TITLE_1_1 is displayed")

        composeTestRule
            .onNodeWithText(TITLE_1_2)
            .assertDoesNotExist()
        Log.e("test $testNumber assert", "$TITLE_1_2 does not exist")
        composeTestRule.waitForTimeout(timeout)

        val indexSong2 = (titles.indexOf(TITLE_1_2) - 3)
            .takeIf { it >= 0 }
            ?: 0
        composeTestRule
            .onNodeWithTag(SONG_LIST_LAZY_COLUMN)
            .performScrollToIndex(indexSong2)
        Log.e("test $testNumber scroll", "songList to index $indexSong2")
        composeTestRule.waitForTimeout(timeout)
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(TITLE_1_2)
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithText(TITLE_1_2)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$TITLE_1_2 is displayed")

        composeTestRule.waitForTimeout(timeout)

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
        composeTestRule.waitForTimeout(timeout)
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(TITLE_1_3)
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithText(TITLE_1_3)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$TITLE_1_3 is displayed")

        composeTestRule.waitForTimeout(timeout)

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
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(TITLE_1_4)
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithText(TITLE_1_4)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$TITLE_1_4 is displayed")
    }

    @Test
    fun test0201_songTextIsOpeningFromSongListCorrectly() {
        val testNumber = 201

        composeTestRule.activityRule.scenario.onActivity {
            selectArtist(ARTIST_1)
        }

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(APP_BAR_TITLE)
                .text?.startEquallyWith(ARTIST_1) ?: false
        }

        val songs = localRepository
            .getSongsByArtistAsList(ARTIST_1)
        val titles = songs
            .map { it.title }
        val songIndex1 = titles.indexOf(TITLE_1_3)
        val song1 = songs[songIndex1]

        composeTestRule.waitForTimeout(timeout)
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(SONG_LIST_LAZY_COLUMN)
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithTag(SONG_LIST_LAZY_COLUMN)
            .performScrollToIndex(songIndex1 - 3)
        Log.e("test $testNumber scroll", "songList to index ${songIndex1 - 3}")
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(TITLE_1_3)
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithText(TITLE_1_3)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$TITLE_1_3 is displayed")
        composeTestRule
            .onNodeWithText(TITLE_1_3)
            .performClick()
        Log.e("test $testNumber click", TITLE_1_3)
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText("${song1.title} (${song1.artist})")
                .isDisplayed()
        }

        composeTestRule
            .onNodeWithText("${song1.title} (${song1.artist})")
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "song title with artist is displayed")
        composeTestRule
            .onNodeWithText(song1.text)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "song text is displayed correctly")
    }

    @Test
    fun test0202_songTextEditorIsOpeningAndClosingCorrectly() {
        val testNumber = 202

        composeTestRule.activityRule.scenario.onActivity {
            selectSongByArtistAndTitle(ARTIST_1, TITLE_1_3)
        }

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText("$TITLE_1_3 ($ARTIST_1)")
                .isDisplayed()
        }

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
            .onNodeWithText(song1.text)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "song text is displayed correctly")
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
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(SONG_TEXT_EDITOR)
                .isDisplayed()
        }

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
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(SONG_TEXT_VIEWER)
                .isDisplayed()
        }

        composeTestRule
            .onNodeWithTag(SONG_TEXT_VIEWER)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$SONG_TEXT_VIEWER is displayed")
        composeTestRule
            .onNodeWithText(song1.text)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "song text is displayed correctly")
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
    fun test0203_songTextMusicButtonsCountEqualsTwo() {
        val testNumber = 203

        composeTestRule.activityRule.scenario.onActivity {
            selectSongByArtistAndTitle(ARTIST_1, TITLE_1_3)
        }

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText("$TITLE_1_3 ($ARTIST_1)")
                .isDisplayed()
        }

        composeTestRule
            .onAllNodesWithTag(MUSIC_BUTTON)
            .assertCountEquals(2)
        Log.e("test $testNumber assert", "$MUSIC_BUTTON count is 2")
    }

    @Test
    fun test0204_songTextLeftAndRightButtonsAreWorkingCorrectly() {
        val testNumber = 204

        composeTestRule.activityRule.scenario.onActivity {
            selectSongByArtistAndTitle(ARTIST_1, TITLE_1_3)
        }

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText("$TITLE_1_3 ($ARTIST_1)")
                .isDisplayed()
        }

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
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText("${song2.title} (${song2.artist})")
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithText("${song2.title} (${song2.artist})")
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "song2 title with artist is displayed")
        composeTestRule
            .onNodeWithText(song2.text)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "song text is displayed correctly")

        composeTestRule
            .onNodeWithTag(LEFT_BUTTON)
            .performClick()
        Log.e("test $testNumber click", LEFT_BUTTON)
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText("${song1.title} (${song1.artist})")
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithText("${song1.title} (${song1.artist})")
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "song1 title with artist is displayed")
    }

    @Test
    fun test0205_songTextAddingAndRemovingFromFavoriteIsWorkingCorrectly() {
        val testNumber = 205

        localRepository.setFavorite(false, ARTIST_1, TITLE_1_3)

        composeTestRule.activityRule.scenario.onActivity {
            selectSongByArtistAndTitle(ARTIST_1, TITLE_1_3)
        }

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText("$TITLE_1_3 ($ARTIST_1)")
                .isDisplayed()
        }

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
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(DELETE_FROM_FAVORITE_BUTTON)
                .isDisplayed()
        }

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
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(ADD_TO_FAVORITE_BUTTON)
                .isDisplayed()
        }

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

        composeTestRule.activityRule.scenario.onActivity {
            selectSongByArtistAndTitle(ARTIST_1, TITLE_1_3)
        }

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText("$TITLE_1_3 ($ARTIST_1)")
                .isDisplayed()
        }

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

        song = localRepository.getSongByArtistAndTitle(ARTIST_1, TITLE_1_3)!!
        assertEquals(song.outOfTheBox, false)

        composeTestRule.activityRule.scenario.onActivity {
            selectSongByArtistAndTitle(ARTIST_1, TITLE_1_3)
        }

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText("$TITLE_1_3 ($ARTIST_1)")
                .isDisplayed()
        }

        composeTestRule
            .onNodeWithTag(UPLOAD_BUTTON)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$UPLOAD_BUTTON is displayed")
        composeTestRule
            .onNodeWithTag(UPLOAD_BUTTON)
            .performClick()
        Log.e("test $testNumber click", UPLOAD_BUTTON)

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(stringConst.uploadToCloudTitle)
                .isDisplayed()
        }

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

        song = song.copy(text = origText)
        localRepository.updateSong(song)

        song = localRepository.getSongByArtistAndTitle(ARTIST_1, TITLE_1_3)!!
        assertEquals(song.outOfTheBox, true)

        assertFalse(ToastsTestImpl.verifyText(stringConst.toastSongIsOutOfTheBox))
        Log.e("test $testNumber toast", "toasts shown")
    }

    @Test
    fun test0208_songTextWarningDialogIsWorkingCorrectly() {
        val testNumber = 208

        composeTestRule.activityRule.scenario.onActivity {
            selectSongByArtistAndTitle(ARTIST_1, TITLE_1_3)
        }

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText("$TITLE_1_3 ($ARTIST_1)")
                .isDisplayed()
        }

        composeTestRule
            .onNodeWithTag(WARNING_BUTTON)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$WARNING_BUTTON is displayed")
        composeTestRule
            .onNodeWithTag(WARNING_BUTTON)
            .performClick()
        Log.e("test $testNumber click", WARNING_BUTTON)
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(stringConst.sendWarningText)
                .isDisplayed()
        }

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
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(WARNING_BUTTON)
                .isDisplayed()
        }

        composeTestRule
            .onNodeWithTag(WARNING_BUTTON)
            .performClick()
        Log.e("test $testNumber click", WARNING_BUTTON)
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(TEXT_FIELD_WARNING_COMMENT)
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithTag(TEXT_FIELD_WARNING_COMMENT)
            .performTextReplacement(WARNING_COMMENT)
        composeTestRule
            .onNodeWithText(stringConst.ok)
            .performClick()
        Log.e("test $testNumber click", stringConst.ok)

        composeTestRule.waitForCondition {
            ToastsTestImpl.verifyText(stringConst.toastCommentCannotBeEmpty)
        }
        composeTestRule.waitForCondition {
            ToastsTestImpl.verifyText(stringConst.toastSendWarningSuccess)
        }
        Log.e("test $testNumber toast", "toasts shown")
    }

    @Test
    fun test0209_songTextSongToTrashDialogIsOpeningCorrectly() {
        val testNumber = 209

        composeTestRule.activityRule.scenario.onActivity {
            selectSongByArtistAndTitle(ARTIST_1, TITLE_1_3)
        }

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText("$TITLE_1_3 ($ARTIST_1)")
                .isDisplayed()
        }

        composeTestRule
            .onNodeWithTag(TRASH_BUTTON)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$TRASH_BUTTON is displayed")
        composeTestRule
            .onNodeWithTag(TRASH_BUTTON)
            .performClick()
        Log.e("test $testNumber click", TRASH_BUTTON)
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(stringConst.songToTrashTitle)
                .isDisplayed()
        }

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
    fun test0301_cloudSearchIsOpeningFromMenuCorrectly() {
        val testNumber = 301

        with (cloudRepository) {
            val list = search("", CloudSearchOrderBy.BY_ID_DESC)
            val titleList = list.map { "${it.artist} - ${it.title}" }
            Log.e("test $testNumber list", titleList.toString())

            composeTestRule.waitForCondition {
                composeTestRule
                    .onNodeWithTag(DRAWER_BUTTON_MAIN)
                    .isDisplayed()
            }

            composeTestRule
                .onNodeWithTag(DRAWER_BUTTON_MAIN)
                .performClick()
            Log.e("test $testNumber click", DRAWER_BUTTON_MAIN)
            composeTestRule.waitForCondition {
                composeTestRule
                    .onNodeWithText(ARTIST_CLOUD_SONGS)
                    .isDisplayed()
            }
            composeTestRule
                .onNodeWithText(ARTIST_CLOUD_SONGS)
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "$ARTIST_CLOUD_SONGS is displayed")
            composeTestRule
                .onNodeWithText(ARTIST_CLOUD_SONGS)
                .performClick()
            Log.e("test $testNumber click", ARTIST_CLOUD_SONGS)
            composeTestRule.waitForCondition {
                composeTestRule
                    .onNodeWithText(CloudSearchOrderBy.BY_ID_DESC.orderByRus)
                    .isDisplayed()
            }
            composeTestRule
                .onNodeWithText(CloudSearchOrderBy.BY_ID_DESC.orderByRus)
                .assertIsDisplayed()
            composeTestRule.waitForCondition {
                composeTestRule
                    .onNodeWithText(list[2].visibleTitleWithRating)
                    .isDisplayed()
            }
            Log.e("test $testNumber assert", "${CloudSearchOrderBy.BY_ID_DESC.orderByRus} is displayed")
            composeTestRule
                .onNodeWithText(list[2].visibleTitleWithRating)
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "${list[2].visibleTitleWithRating} is displayed")
        }
    }

    @Test
    fun test0302_cloudSearchOfflineIsWorkingCorrectly() {
        val testNumber = 302

        with (cloudRepository) {
            composeTestRule.activityRule.scenario.onActivity {
                CommonViewModel
                    .getStoredInstance()
                    ?.submitAction(SelectScreen(ScreenVariant.CloudSearch()))
            }

            composeTestRule.waitForCondition {
                composeTestRule
                    .onNodeWithText(ARTIST_CLOUD_SONGS)
                    .isDisplayed()
            }

            isOnline = false
            composeTestRule
                .onNodeWithTag(SEARCH_BUTTON)
                .performClick()
            Log.e("test $testNumber click", SEARCH_BUTTON)
            composeTestRule.waitForCondition {
                composeTestRule
                    .onNodeWithText(stringConst.fetchDataError)
                    .isDisplayed()
            }
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

        with (cloudRepository) {
            composeTestRule.activityRule.scenario.onActivity {
                CommonViewModel
                    .getStoredInstance()
                    ?.submitAction(SelectScreen(ScreenVariant.CloudSearch()))
            }

            composeTestRule.waitForCondition {
                composeTestRule
                    .onNodeWithTag(TEXT_FIELD_SEARCH_FOR)
                    .isDisplayed()
            }

            composeTestRule
                .onNodeWithTag(TEXT_FIELD_SEARCH_FOR)
                .performTextReplacement("Ло")
            Log.e("test $testNumber input", "Ло")
            Espresso.closeSoftKeyboard()
            composeTestRule.waitForTimeout(timeout)
            composeTestRule
                .onNodeWithTag(SEARCH_BUTTON)
                .performClick()
            Log.e("test $testNumber click", SEARCH_BUTTON)

            val list = search("Ло", CloudSearchOrderBy.BY_ID_DESC)
            val titleList = list.map { "${it.artist} - ${it.title}" }
            Log.e("test $testNumber list", titleList.toString())

            composeTestRule.waitForCondition {
                composeTestRule
                    .onNodeWithText(list[2].visibleTitleWithRating)
                    .isDisplayed()
            }

            composeTestRule
                .onNodeWithText(list[2].visibleTitleWithRating)
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "${list[2].visibleTitleWithRating} is displayed")
        }
    }

    @Test
    fun test0304_cloudSearchOrderBySpinnerValuesAreDisplayingCorrectly() {
        val testNumber = 304

        composeTestRule.activityRule.scenario.onActivity {
            CommonViewModel
                .getStoredInstance()
                ?.submitAction(SelectScreen(ScreenVariant.CloudSearch()))
        }

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(CloudSearchOrderBy.BY_ID_DESC.orderByRus)
                .isDisplayed()
        }

        composeTestRule
            .onNodeWithText(CloudSearchOrderBy.BY_ID_DESC.orderByRus)
            .performClick()
        Log.e("test $testNumber click", CloudSearchOrderBy.BY_ID_DESC.orderByRus)
        composeTestRule.waitForCondition {
            composeTestRule
                .onAllNodesWithText(CloudSearchOrderBy.BY_ID_DESC.orderByRus)
                .fetchSemanticsNodes()
                .count() == 2
        }
        composeTestRule
            .onAllNodesWithText(CloudSearchOrderBy.BY_ID_DESC.orderByRus)
            .assertCountEquals(2)
        Log.e("test $testNumber assert", "${CloudSearchOrderBy.BY_ID_DESC.orderByRus} count is 2")
        composeTestRule
            .onNodeWithText(CloudSearchOrderBy.BY_ARTIST.orderByRus)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${CloudSearchOrderBy.BY_ARTIST.orderByRus} is displayed")
        composeTestRule
            .onNodeWithText(CloudSearchOrderBy.BY_TITLE.orderByRus)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${CloudSearchOrderBy.BY_TITLE.orderByRus} is displayed")
    }

    @Test
    fun test0305_cloudSearchOrderingByTitleIsWorkingCorrectly() {
        val testNumber = 305

        with (cloudRepository) {
            composeTestRule.activityRule.scenario.onActivity {
                CommonViewModel
                    .getStoredInstance()
                    ?.submitAction(SelectScreen(ScreenVariant.CloudSearch()))
            }

            composeTestRule.waitForCondition {
                composeTestRule
                    .onNodeWithTag(TEXT_FIELD_SEARCH_FOR)
                    .isDisplayed()
            }

            composeTestRule
                .onNodeWithTag(TEXT_FIELD_SEARCH_FOR)
                .performTextReplacement("Ло")
            Log.e("test $testNumber input", "Ло")
            composeTestRule
                .onNodeWithTag(SEARCH_BUTTON)
                .performClick()
            Log.e("test $testNumber click", SEARCH_BUTTON)

            composeTestRule
                .onNodeWithText(CloudSearchOrderBy.BY_ID_DESC.orderByRus)
                .performClick()
            Log.e("test $testNumber click", CloudSearchOrderBy.BY_ID_DESC.orderByRus)
            composeTestRule.waitForCondition {
                composeTestRule
                    .onNodeWithText(CloudSearchOrderBy.BY_TITLE.orderByRus)
                    .isDisplayed()
            }
            composeTestRule
                .onNodeWithText(CloudSearchOrderBy.BY_TITLE.orderByRus)
                .performClick()
            Log.e("test $testNumber click", CloudSearchOrderBy.BY_TITLE.orderByRus)

            val list = search("Ло", CloudSearchOrderBy.BY_TITLE)
            val titleList = list.map { "${it.artist} - ${it.title}" }
            Log.e("test $testNumber list", titleList.toString())

            composeTestRule.waitForCondition {
                composeTestRule
                    .onNodeWithText(list[2].visibleTitleWithRating)
                    .isDisplayed()
            }
            composeTestRule
                .onNodeWithText(list[2].visibleTitleWithRating)
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "${list[2].visibleTitleWithRating} is displayed")
        }
    }

    @Test
    fun test0306_cloudSearchOrderingByArtistIsWorkingCorrectly() {
        val testNumber = 306

        with (cloudRepository) {
            composeTestRule.activityRule.scenario.onActivity {
                CommonViewModel
                    .getStoredInstance()
                    ?.submitAction(SelectScreen(ScreenVariant.CloudSearch()))
            }

            composeTestRule.waitForCondition {
                composeTestRule
                    .onNodeWithTag(TEXT_FIELD_SEARCH_FOR)
                    .isDisplayed()
            }
            composeTestRule
                .onNodeWithTag(TEXT_FIELD_SEARCH_FOR)
                .performTextReplacement("Ло")
            Log.e("test $testNumber input", "Ло")
            composeTestRule
                .onNodeWithTag(SEARCH_BUTTON)
                .performClick()
            Log.e("test $testNumber click", SEARCH_BUTTON)

            composeTestRule
                .onNodeWithText(CloudSearchOrderBy.BY_ID_DESC.orderByRus)
                .performClick()
            Log.e("test $testNumber click", CloudSearchOrderBy.BY_ID_DESC.orderByRus)
            composeTestRule.waitForCondition {
                composeTestRule
                    .onNodeWithText(CloudSearchOrderBy.BY_ARTIST.orderByRus)
                    .isDisplayed()
            }
            composeTestRule
                .onNodeWithText(CloudSearchOrderBy.BY_ARTIST.orderByRus)
                .performClick()
            Log.e("test $testNumber click", CloudSearchOrderBy.BY_ARTIST.orderByRus)

            val list = search("Ло", CloudSearchOrderBy.BY_ARTIST)
            val titleList = list.map { "${it.artist} - ${it.title}" }
            Log.e("test $testNumber list", titleList.toString())

            composeTestRule.waitForCondition {
                composeTestRule
                    .onNodeWithText(list[2].visibleTitleWithRating)
                    .isDisplayed()
            }
            composeTestRule
                .onNodeWithText(list[2].visibleTitleWithRating)
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "${list[2].visibleTitleWithRating} is displayed")
        }
    }

    @Test
    fun test0307_cloudSearchQueryWithEmptyResultIsWorkingCorrectly() {
        val testNumber = 307

        composeTestRule.activityRule.scenario.onActivity {
            CommonViewModel
                .getStoredInstance()
                ?.submitAction(SelectScreen(ScreenVariant.CloudSearch()))
        }

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(TEXT_FIELD_SEARCH_FOR)
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithTag(TEXT_FIELD_SEARCH_FOR)
            .performTextReplacement("Хзщшг")
        Log.e("test $testNumber input", "Хзщшг")
        Espresso.closeSoftKeyboard()
        composeTestRule.waitForTimeout(timeout)
        composeTestRule
            .onNodeWithTag(SEARCH_BUTTON)
            .performClick()
        Log.e("test $testNumber click", SEARCH_BUTTON)
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(stringConst.listIsEmpty)
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithText(stringConst.listIsEmpty)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${stringConst.listIsEmpty} is displayed")
    }

    @Test
    fun test0401_cloudSongTextIsOpeningFromCloudSearchCorrectly() {
        val testNumber = 401

        with (cloudRepository) {
            val list = search("", CloudSearchOrderBy.BY_ID_DESC)
            val titleList = list.map { "${it.artist} - ${it.title}" }
            Log.e("test $testNumber list", titleList.toString())

            composeTestRule.activityRule.scenario.onActivity {
                CommonViewModel
                    .getStoredInstance()
                    ?.submitAction(SelectScreen(ScreenVariant.CloudSearch()))
            }

            composeTestRule.waitForCondition {
                composeTestRule
                    .onNodeWithText(list[2].visibleTitleWithRating)
                    .isDisplayed()
            }
            composeTestRule
                .onNodeWithText(list[2].visibleTitleWithRating)
                .performClick()
            Log.e("test $testNumber click", list[2].visibleTitleWithRating)
            composeTestRule.waitForCondition {
                composeTestRule
                    .onNodeWithText(list[2].visibleTitleWithArtistAndRating)
                    .isDisplayed()
            }
            composeTestRule
                .onNodeWithText(list[2].visibleTitleWithArtistAndRating)
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "${list[2].visibleTitleWithArtistAndRating} is displayed")
            composeTestRule
                .onNodeWithText(list[2].text)
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "song text is displayed correctly")
        }
    }

    @Test
    fun test0402_cloudSongTextAllButtonsAreDisplaying() {
        val testNumber = 402

        composeTestRule.activityRule.scenario.onActivity {
            CommonViewModel
                .getStoredInstance()
                ?.submitAction(SelectScreen(ScreenVariant.CloudSearch()))
        }

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(ARTIST_CLOUD_SONGS)
                .isDisplayed()
        }

        composeTestRule.activityRule.scenario.onActivity {
            CommonViewModel
                .getStoredInstance()
                ?.submitAction(SelectScreen(ScreenVariant.CloudSongText(2)))
        }

        composeTestRule.waitForCondition {
            composeTestRule
                .onAllNodesWithTag(MUSIC_BUTTON)
                .fetchSemanticsNodes()
                .count() == 2
        }
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

        with (cloudRepository) {
            val list = search("", CloudSearchOrderBy.BY_ID_DESC)
            val titleList = list.map { "${it.artist} - ${it.title}" }
            Log.e("test $testNumber list", titleList.toString())

            composeTestRule.activityRule.scenario.onActivity {
                CommonViewModel
                    .getStoredInstance()
                    ?.submitAction(SelectScreen(ScreenVariant.CloudSearch()))
            }

            composeTestRule.waitForCondition {
                composeTestRule
                    .onNodeWithText(ARTIST_CLOUD_SONGS)
                    .isDisplayed()
            }

            composeTestRule.activityRule.scenario.onActivity {
                CommonViewModel
                    .getStoredInstance()
                    ?.submitAction(SelectScreen(ScreenVariant.CloudSongText(2)))
            }

            composeTestRule.waitForCondition {
                composeTestRule
                    .onNodeWithText(list[2].visibleTitleWithArtistAndRating)
                    .isDisplayed()
            }
            composeTestRule
                .onNodeWithText(list[2].visibleTitleWithArtistAndRating)
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "${list[2].visibleTitleWithArtistAndRating} is displayed")
            composeTestRule
                .onNodeWithText(list[2].text)
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "song text is displayed correctly")
            composeTestRule
                .onNodeWithTag(NUMBER_LABEL)
                .assertTextContains("3 /", substring = true)
            Log.e("test $testNumber assert", "$NUMBER_LABEL contains '3 /'")

            composeTestRule
                .onNodeWithTag(RIGHT_BUTTON)
                .performClick()
            Log.e("test $testNumber click", RIGHT_BUTTON)

            composeTestRule.waitForCondition {
                composeTestRule
                    .onNodeWithText(list[3].visibleTitleWithArtistAndRating)
                    .isDisplayed()
            }
            composeTestRule
                .onNodeWithText(list[3].visibleTitleWithArtistAndRating)
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "${list[3].visibleTitleWithArtistAndRating} is displayed")
            composeTestRule
                .onNodeWithText(list[3].text)
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "song text is displayed correctly")
            composeTestRule
                .onNodeWithTag(NUMBER_LABEL)
                .assertTextContains("4 /", substring = true)
            Log.e("test $testNumber assert", "$NUMBER_LABEL contains '4 /'")

            composeTestRule
                .onNodeWithTag(LEFT_BUTTON)
                .performClick()
            Log.e("test $testNumber click", LEFT_BUTTON)

            composeTestRule.waitForCondition {
                composeTestRule
                    .onNodeWithText(list[2].visibleTitleWithArtistAndRating)
                    .isDisplayed()
            }
            composeTestRule
                .onNodeWithText(list[2].visibleTitleWithArtistAndRating)
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "${list[2].visibleTitleWithArtistAndRating} is displayed")
            composeTestRule
                .onNodeWithText(list[2].text)
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "song text is displayed correctly")
            composeTestRule
                .onNodeWithTag(NUMBER_LABEL)
                .assertTextContains("3 /", substring = true)
            Log.e("test $testNumber assert", "$NUMBER_LABEL contains '3 /'")
        }
    }

    @Test
    fun test0404_cloudSongTextWarningDialogIsWorkingCorrectly() {
        val testNumber = 404

        composeTestRule.activityRule.scenario.onActivity {
            CommonViewModel
                .getStoredInstance()
                ?.submitAction(SelectScreen(ScreenVariant.CloudSearch()))
        }

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(ARTIST_CLOUD_SONGS)
                .isDisplayed()
        }

        composeTestRule.activityRule.scenario.onActivity {
            CommonViewModel
                .getStoredInstance()
                ?.submitAction(SelectScreen(ScreenVariant.CloudSongText(2)))
        }

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(WARNING_BUTTON)
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithTag(WARNING_BUTTON)
            .performClick()
        Log.e("test $testNumber click", WARNING_BUTTON)

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(stringConst.sendWarningText)
                .isDisplayed()
        }
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

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(stringConst.sendWarningText)
                .isNotDisplayed()
        }

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

        with (cloudRepository) {
            val list = search("", CloudSearchOrderBy.BY_ID_DESC)
            val titleList = list.map { "${it.artist} - ${it.title}" }
            Log.e("test $testNumber list", titleList.toString())

            composeTestRule.activityRule.scenario.onActivity {
                CommonViewModel
                    .getStoredInstance()
                    ?.submitAction(SelectScreen(ScreenVariant.CloudSearch()))
            }

            composeTestRule.waitForCondition {
                composeTestRule
                    .onNodeWithText(ARTIST_CLOUD_SONGS)
                    .isDisplayed()
            }

            composeTestRule.activityRule.scenario.onActivity {
                CommonViewModel
                    .getStoredInstance()
                    ?.submitAction(SelectScreen(ScreenVariant.CloudSongText(2)))
            }

            composeTestRule.waitForCondition {
                composeTestRule
                    .onNodeWithTag(DOWNLOAD_BUTTON)
                    .isDisplayed()
            }

            composeTestRule
                .onNodeWithTag(DOWNLOAD_BUTTON)
                .performClick()
            Log.e("test $testNumber click", DOWNLOAD_BUTTON)
            composeTestRule.waitForCondition {
                localRepository
                    .getSongsByArtistAsList(list[2].artist)
                    .map { it.title }
                    .contains(list[2].visibleTitle)
            }
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

        with (cloudRepository) {
            val list = search("", CloudSearchOrderBy.BY_ID_DESC)
            val titleList = list.map { "${it.artist} - ${it.title}" }
            Log.e("test $testNumber list", titleList.toString())

            composeTestRule.activityRule.scenario.onActivity {
                CommonViewModel
                    .getStoredInstance()
                    ?.submitAction(SelectScreen(ScreenVariant.CloudSearch()))
            }

            composeTestRule.waitForCondition {
                composeTestRule
                    .onNodeWithText(ARTIST_CLOUD_SONGS)
                    .isDisplayed()
            }

            composeTestRule.activityRule.scenario.onActivity {
                CommonViewModel
                    .getStoredInstance()
                    ?.submitAction(SelectScreen(ScreenVariant.CloudSongText(2)))
            }

            val cloudSong2 = list[2].let {
                it.copy(likeCount = it.likeCount + 1)
            }

            composeTestRule.waitForCondition {
                composeTestRule
                    .onNodeWithTag(LIKE_BUTTON)
                    .isDisplayed()
            }
            composeTestRule
                .onNodeWithTag(LIKE_BUTTON)
                .performClick()
            Log.e("test $testNumber click", LIKE_BUTTON)
            composeTestRule.waitForCondition {
                composeTestRule
                    .onNodeWithText(cloudSong2.visibleTitleWithArtistAndRating)
                    .isDisplayed()
            }
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

            composeTestRule.waitForCondition {
                composeTestRule
                    .onNodeWithText(cloudSong2.visibleTitleWithRating)
                    .isDisplayed()
            }
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

        with (cloudRepository) {
            val list = search("", CloudSearchOrderBy.BY_ID_DESC)
            val titleList = list.map { "${it.artist} - ${it.title}" }
            Log.e("test $testNumber list", titleList.toString())

            composeTestRule.activityRule.scenario.onActivity {
                CommonViewModel
                    .getStoredInstance()
                    ?.submitAction(SelectScreen(ScreenVariant.CloudSearch()))
            }

            composeTestRule.waitForCondition {
                composeTestRule
                    .onNodeWithText(ARTIST_CLOUD_SONGS)
                    .isDisplayed()
            }

            composeTestRule.activityRule.scenario.onActivity {
                CommonViewModel
                    .getStoredInstance()
                    ?.submitAction(SelectScreen(ScreenVariant.CloudSongText(3)))
            }

            val cloudSong3 = list[3].let {
                it.copy(dislikeCount = it.dislikeCount + 1)
            }

            composeTestRule.waitForCondition {
                composeTestRule
                    .onNodeWithTag(DISLIKE_BUTTON)
                    .isDisplayed()
            }
            composeTestRule
                .onNodeWithTag(DISLIKE_BUTTON)
                .performClick()
            Log.e("test $testNumber click", DISLIKE_BUTTON)
            composeTestRule.waitForCondition {
                composeTestRule
                    .onNodeWithText(cloudSong3.visibleTitleWithArtistAndRating)
                    .isDisplayed()
            }
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

            composeTestRule.waitForCondition {
                composeTestRule
                    .onNodeWithText(cloudSong3.visibleTitleWithRating)
                    .isDisplayed()
            }
            composeTestRule
                .onNodeWithText(cloudSong3.visibleTitleWithRating)
                .assertIsDisplayed()
            Log.e("test $testNumber assert", "${cloudSong3.visibleTitleWithRating} is displayed")

            assertTrue(ToastsTestImpl.verifyText(stringConst.toastVoteSuccess))
            Log.e("test $testNumber toast", "toasts shown")
        }
    }

    @Test
    fun test0500_addingSongWithUploadingToCloudAndDeletingIsWorkingCorrectly() {
        val testNumber = 500

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(DRAWER_BUTTON_MAIN)
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithTag(DRAWER_BUTTON_MAIN)
            .performClick()
        Log.e("test $testNumber click", DRAWER_BUTTON_MAIN)
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(ARTIST_ADD_SONG)
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithText(ARTIST_ADD_SONG)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$ARTIST_ADD_SONG is displayed")
        composeTestRule
            .onNodeWithText(ARTIST_ADD_SONG)
            .performClick()
        Log.e("test $testNumber click", ARTIST_ADD_SONG)

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(stringConst.save)
                .isDisplayed()
        }
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

        composeTestRule
            .onNodeWithTag(TEXT_FIELD_TITLE)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$TEXT_FIELD_TITLE is displayed")
        composeTestRule
            .onNodeWithTag(TEXT_FIELD_TITLE)
            .performTextReplacement(TITLE_NEW)
        Log.e("test $testNumber input", TITLE_NEW)

        composeTestRule
            .onNodeWithTag(TEXT_FIELD_TEXT)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$TEXT_FIELD_TEXT is displayed")
        composeTestRule
            .onNodeWithTag(TEXT_FIELD_TEXT)
            .performTextReplacement(TEXT_NEW)
        Log.e("test $testNumber input", TEXT_NEW)

        composeTestRule
            .onNodeWithText(stringConst.save)
            .performClick()
        Log.e("test $testNumber click", stringConst.save)

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(stringConst.uploadToCloudTitle)
                .isDisplayed()
        }
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

        composeTestRule.waitForTimeout(timeout)

        with (cloudRepository) {
            composeTestRule.waitForCondition {
                val list = search("", CloudSearchOrderBy.BY_ID_DESC)
                val cloudSong = list[0]
                Log.e("cloud song", cloudSong.artist)
                cloudSong.artist == ARTIST_NEW
            }

            val list = search("", CloudSearchOrderBy.BY_ID_DESC)
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
            .onNodeWithText(TEXT_NEW)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "song text is displayed correctly")

        val song = localRepository.getSongByArtistAndTitle(ARTIST_NEW, TITLE_NEW)
        assert(song != null)
        assert(song!!.text == TEXT_NEW)
        Log.e("test $testNumber assert", "new song exists and text matches")

        composeTestRule
            .onNodeWithTag(TRASH_BUTTON)
            .performClick()
        Log.e("test $testNumber click", TRASH_BUTTON)

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(stringConst.ok)
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithText(stringConst.ok)
            .performClick()
        Log.e("test $testNumber click", stringConst.ok)

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(stringConst.listIsEmpty)
                .isDisplayed()
        }
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
    fun test0501_addSongScreenIsClosingCorrectly_systemBack() {
        val testNumber = 501

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(DRAWER_BUTTON_MAIN)
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithTag(DRAWER_BUTTON_MAIN)
            .performClick()
        Log.e("test $testNumber click", DRAWER_BUTTON_MAIN)
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(ARTIST_ADD_SONG)
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithText(ARTIST_ADD_SONG)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$ARTIST_ADD_SONG is displayed")
        composeTestRule
            .onNodeWithText(ARTIST_ADD_SONG)
            .performClick()
        Log.e("test $testNumber click", ARTIST_ADD_SONG)

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(stringConst.save)
                .isDisplayed()
        }
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

        Espresso.closeSoftKeyboard()
        composeTestRule.waitForTimeout(timeout)
        composeTestRule.pressBackUnconditionally()
        Log.e("test $testNumber action", "press back")

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(APP_BAR_TITLE)
                .isDisplayed()
        }

        val actualArtist = composeTestRule
            .onNodeWithTag(APP_BAR_TITLE)
            .text ?: "error"

        assertTrue(actualArtist.startEquallyWith(settingsRepository.defaultArtist))
        Log.e("test $testNumber assert", "app bar title starts equally with ${settingsRepository.defaultArtist}")

        composeTestRule.pressBackUnconditionally()
        Log.e("test $testNumber action", "press back")

        composeTestRule.waitForCondition {
            composeTestRule.activityRule.scenario.state == Lifecycle.State.DESTROYED
        }
        assertEquals(composeTestRule.activityRule.scenario.state, Lifecycle.State.DESTROYED)
        Log.e("test $testNumber assert", "activity is destroyed")
    }

    @Test
    fun test0502_addSongScreenIsClosingCorrectly_appBack() {
        val testNumber = 502

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(DRAWER_BUTTON_MAIN)
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithTag(DRAWER_BUTTON_MAIN)
            .performClick()
        Log.e("test $testNumber click", DRAWER_BUTTON_MAIN)
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(ARTIST_ADD_SONG)
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithText(ARTIST_ADD_SONG)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$ARTIST_ADD_SONG is displayed")
        composeTestRule
            .onNodeWithText(ARTIST_ADD_SONG)
            .performClick()
        Log.e("test $testNumber click", ARTIST_ADD_SONG)

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(stringConst.save)
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithText(stringConst.save)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${stringConst.save} is displayed")

        composeTestRule
            .onNodeWithTag(BACK_BUTTON)
            .performClick()
        Log.e("test $testNumber click", BACK_BUTTON)

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(APP_BAR_TITLE)
                .isDisplayed()
        }

        val actualArtist = composeTestRule
            .onNodeWithTag(APP_BAR_TITLE)
            .text ?: "error"

        assertTrue(actualArtist.startEquallyWith(settingsRepository.defaultArtist))
        Log.e("test $testNumber assert", "app bar title starts equally with ${settingsRepository.defaultArtist}")

        composeTestRule.pressBackUnconditionally()
        Log.e("test $testNumber action", "press back")

        composeTestRule.waitForCondition {
            composeTestRule.activityRule.scenario.state == Lifecycle.State.DESTROYED
        }
        assertEquals(composeTestRule.activityRule.scenario.state, Lifecycle.State.DESTROYED)
        Log.e("test $testNumber assert", "activity is destroyed")
    }

    @Test
    fun test0600_settingsScreenIsDisplayingAndClosingCorrectly_systemBack() {
        val testNumber = 600

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(SETTINGS_BUTTON)
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithTag(SETTINGS_BUTTON)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$SETTINGS_BUTTON is displayed")
        composeTestRule
            .onNodeWithTag(SETTINGS_BUTTON)
            .performClick()
        Log.e("test $testNumber click", SETTINGS_BUTTON)

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(APP_BAR_TITLE)
                .isDisplayed()
        }

        val actualTitle = composeTestRule
            .onNodeWithTag(APP_BAR_TITLE)
            .text ?: "null"
        assertTrue(actualTitle.startEquallyWith(stringConst.titleSettings))
        Log.e("test $testNumber assert", "app bar title starts equally with ${stringConst.titleSettings}")

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

        composeTestRule
            .onNodeWithTag(TEXT_FIELD_SCROLL_SPEED)
            .performTextReplacement("2.0")
        Log.e("test $testNumber input", ARTIST_NEW)

        Espresso.closeSoftKeyboard()
        composeTestRule.waitForTimeout(timeout)
        composeTestRule.pressBackUnconditionally()
        Log.e("test $testNumber action", "press back")

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(APP_BAR_TITLE)
                .isDisplayed()
        }

        val actualArtist = composeTestRule
            .onNodeWithTag(APP_BAR_TITLE)
            .text ?: "error"

        assertTrue(actualArtist.startEquallyWith(settingsRepository.defaultArtist))
        Log.e("test $testNumber assert", "app bar title starts equally with ${settingsRepository.defaultArtist}")

        composeTestRule.pressBackUnconditionally()
        Log.e("test $testNumber action", "press back")

        composeTestRule.waitForCondition {
            composeTestRule.activityRule.scenario.state == Lifecycle.State.DESTROYED
        }
        assertEquals(composeTestRule.activityRule.scenario.state, Lifecycle.State.DESTROYED)
        Log.e("test $testNumber assert", "activity is destroyed")
    }

    @Test
    fun test0601_settingsScreenIsDisplayingAndClosingCorrectly_appBack() {
        val testNumber = 601

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(SETTINGS_BUTTON)
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithTag(SETTINGS_BUTTON)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$SETTINGS_BUTTON is displayed")
        composeTestRule
            .onNodeWithTag(SETTINGS_BUTTON)
            .performClick()
        Log.e("test $testNumber click", SETTINGS_BUTTON)

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(APP_BAR_TITLE)
                .isDisplayed()
        }

        val actualTitle = composeTestRule
            .onNodeWithTag(APP_BAR_TITLE)
            .text ?: "null"
        assertTrue(actualTitle.startEquallyWith(stringConst.titleSettings))
        Log.e("test $testNumber assert", "app bar title starts equally with ${stringConst.titleSettings}")

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

        composeTestRule
            .onNodeWithTag(BACK_BUTTON)
            .performClick()
        Log.e("test $testNumber click", BACK_BUTTON)

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(APP_BAR_TITLE)
                .isDisplayed()
        }

        val actualArtist = composeTestRule
            .onNodeWithTag(APP_BAR_TITLE)
            .text ?: "error"

        assertTrue(actualArtist.startEquallyWith(settingsRepository.defaultArtist))
        Log.e("test $testNumber assert", "app bar title starts equally with ${settingsRepository.defaultArtist}")

        composeTestRule.pressBackUnconditionally()
        Log.e("test $testNumber action", "press back")

        composeTestRule.waitForCondition {
            composeTestRule.activityRule.scenario.state == Lifecycle.State.DESTROYED
        }
        assertEquals(composeTestRule.activityRule.scenario.state, Lifecycle.State.DESTROYED)
        Log.e("test $testNumber assert", "activity is destroyed")
    }

    @Test
    fun test0700_donationScreenIsDisplayingAndClosingCorrectly_systemBack() {
        val testNumber = 700

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(DRAWER_BUTTON_MAIN)
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithTag(DRAWER_BUTTON_MAIN)
            .performClick()
        Log.e("test $testNumber click", DRAWER_BUTTON_MAIN)
        composeTestRule
            .onNodeWithText(ARTIST_DONATION)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$ARTIST_DONATION is displayed")
        composeTestRule
            .onNodeWithText(ARTIST_DONATION)
            .performClick()
        Log.e("test $testNumber click", ARTIST_DONATION)

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(APP_BAR_TITLE)
                .isDisplayed()
        }

        val actualTitle = composeTestRule
            .onNodeWithTag(APP_BAR_TITLE)
            .text ?: "null"
        assertTrue(actualTitle.startEquallyWith(ARTIST_DONATION))
        Log.e("test $testNumber assert", "app bar title starts equally with $ARTIST_DONATION")

        DONATIONS
            .map { donationLabel(it) }
            .forEach {
                composeTestRule
                    .onNodeWithText(it)
                    .assertIsDisplayed()
                Log.e("test $testNumber assert", "$it is displayed")
            }

        composeTestRule.pressBackUnconditionally()
        Log.e("test $testNumber action", "press back")

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(APP_BAR_TITLE)
                .isDisplayed()
        }

        val actualArtist = composeTestRule
            .onNodeWithTag(APP_BAR_TITLE)
            .text ?: "error"

        assertTrue(actualArtist.startEquallyWith(settingsRepository.defaultArtist))
        Log.e("test $testNumber assert", "app bar title starts equally with ${settingsRepository.defaultArtist}")

        composeTestRule.pressBackUnconditionally()
        Log.e("test $testNumber action", "press back")

        composeTestRule.waitForCondition {
            composeTestRule.activityRule.scenario.state == Lifecycle.State.DESTROYED
        }
        assertEquals(composeTestRule.activityRule.scenario.state, Lifecycle.State.DESTROYED)
        Log.e("test $testNumber assert", "activity is destroyed")
    }

    @Test
    fun test0701_donationScreenIsDisplayingAndClosingCorrectly_appBack() {
        val testNumber = 701

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(DRAWER_BUTTON_MAIN)
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithTag(DRAWER_BUTTON_MAIN)
            .performClick()
        Log.e("test $testNumber click", DRAWER_BUTTON_MAIN)
        composeTestRule
            .onNodeWithText(ARTIST_DONATION)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$ARTIST_DONATION is displayed")
        composeTestRule
            .onNodeWithText(ARTIST_DONATION)
            .performClick()
        Log.e("test $testNumber click", ARTIST_DONATION)

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(APP_BAR_TITLE)
                .isDisplayed()
        }

        val actualTitle = composeTestRule
            .onNodeWithTag(APP_BAR_TITLE)
            .text ?: "null"
        assertTrue(actualTitle.startEquallyWith(ARTIST_DONATION))
        Log.e("test $testNumber assert", "app bar title starts equally with $ARTIST_DONATION")

        DONATIONS
            .map { donationLabel(it) }
            .forEach {
                composeTestRule
                    .onNodeWithText(it)
                    .assertIsDisplayed()
                Log.e("test $testNumber assert", "$it is displayed")
            }

        composeTestRule
            .onNodeWithTag(BACK_BUTTON)
            .performClick()
        Log.e("test $testNumber click", BACK_BUTTON)

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(APP_BAR_TITLE)
                .isDisplayed()
        }

        val actualArtist = composeTestRule
            .onNodeWithTag(APP_BAR_TITLE)
            .text ?: "error"

        assertTrue(actualArtist.startEquallyWith(settingsRepository.defaultArtist))
        Log.e("test $testNumber assert", "app bar title starts equally with ${settingsRepository.defaultArtist}")

        composeTestRule.pressBackUnconditionally()
        Log.e("test $testNumber action", "press back")

        composeTestRule.waitForCondition {
            composeTestRule.activityRule.scenario.state == Lifecycle.State.DESTROYED
        }
        assertEquals(composeTestRule.activityRule.scenario.state, Lifecycle.State.DESTROYED)
        Log.e("test $testNumber assert", "activity is destroyed")
    }

    @Test
    fun test0800_addArtistScreenIsDisplayingAndClosingCorrectly_systemBack() {
        val testNumber = 800

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(DRAWER_BUTTON_MAIN)
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithTag(DRAWER_BUTTON_MAIN)
            .performClick()
        Log.e("test $testNumber click", DRAWER_BUTTON_MAIN)
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(ARTIST_ADD_ARTIST)
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithText(ARTIST_ADD_ARTIST)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$ARTIST_ADD_ARTIST is displayed")
        composeTestRule
            .onNodeWithText(ARTIST_ADD_ARTIST)
            .performClick()
        Log.e("test $testNumber click", ARTIST_ADD_ARTIST)

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(APP_BAR_TITLE)
                .isDisplayed()
        }

        val actualTitle = composeTestRule
            .onNodeWithTag(APP_BAR_TITLE)
            .text ?: "null"
        assertTrue(actualTitle.startEquallyWith(ARTIST_ADD_ARTIST))
        Log.e("test $testNumber assert", "app bar title starts equally with $ARTIST_ADD_ARTIST")

        composeTestRule
            .onNodeWithText(stringConst.addArtistManual)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${stringConst.addArtistManual} is displayed")
        composeTestRule
            .onNodeWithText(stringConst.choose)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${stringConst.choose} is displayed")

        composeTestRule.pressBackUnconditionally()
        Log.e("test $testNumber action", "press back")

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(APP_BAR_TITLE)
                .isDisplayed()
        }

        val actualArtist = composeTestRule
            .onNodeWithTag(APP_BAR_TITLE)
            .text ?: "error"

        assertTrue(actualArtist.startEquallyWith(settingsRepository.defaultArtist))
        Log.e("test $testNumber assert", "app bar title starts equally with ${settingsRepository.defaultArtist}")

        composeTestRule.pressBackUnconditionally()
        Log.e("test $testNumber action", "press back")

        composeTestRule.waitForCondition {
            composeTestRule.activityRule.scenario.state == Lifecycle.State.DESTROYED
        }
        assertEquals(composeTestRule.activityRule.scenario.state, Lifecycle.State.DESTROYED)
        Log.e("test $testNumber assert", "activity is destroyed")
    }

    @Test
    fun test0801_addArtistScreenIsDisplayingAndClosingCorrectly_appBack() {
        val testNumber = 801

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(DRAWER_BUTTON_MAIN)
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithTag(DRAWER_BUTTON_MAIN)
            .performClick()
        Log.e("test $testNumber click", DRAWER_BUTTON_MAIN)
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(ARTIST_ADD_ARTIST)
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithText(ARTIST_ADD_ARTIST)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$ARTIST_ADD_ARTIST is displayed")
        composeTestRule
            .onNodeWithText(ARTIST_ADD_ARTIST)
            .performClick()
        Log.e("test $testNumber click", ARTIST_ADD_ARTIST)

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(APP_BAR_TITLE)
                .isDisplayed()
        }

        val actualTitle = composeTestRule
            .onNodeWithTag(APP_BAR_TITLE)
            .text ?: "null"
        assertTrue(actualTitle.startEquallyWith(ARTIST_ADD_ARTIST))
        Log.e("test $testNumber assert", "app bar title starts equally with $ARTIST_ADD_ARTIST")

        composeTestRule
            .onNodeWithText(stringConst.addArtistManual)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${stringConst.addArtistManual} is displayed")
        composeTestRule
            .onNodeWithText(stringConst.choose)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${stringConst.choose} is displayed")

        composeTestRule
            .onNodeWithTag(BACK_BUTTON)
            .performClick()
        Log.e("test $testNumber click", BACK_BUTTON)

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(APP_BAR_TITLE)
                .isDisplayed()
        }

        val actualArtist = composeTestRule
            .onNodeWithTag(APP_BAR_TITLE)
            .text ?: "error"

        assertTrue(actualArtist.startEquallyWith(settingsRepository.defaultArtist))
        Log.e("test $testNumber assert", "app bar title starts equally with ${settingsRepository.defaultArtist}")

        composeTestRule.pressBackUnconditionally()
        Log.e("test $testNumber action", "press back")

        composeTestRule.waitForCondition {
            composeTestRule.activityRule.scenario.state == Lifecycle.State.DESTROYED
        }
        assertEquals(composeTestRule.activityRule.scenario.state, Lifecycle.State.DESTROYED)
        Log.e("test $testNumber assert", "activity is destroyed")
    }

    @Test
    fun test0900_favoriteScreenIsWorkingCorrectly() {
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

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(DRAWER_BUTTON_MAIN)
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithTag(DRAWER_BUTTON_MAIN)
            .performClick()
        Log.e("test $testNumber click", DRAWER_BUTTON_MAIN)
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(ARTIST_FAVORITE)
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithText(ARTIST_FAVORITE)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$ARTIST_FAVORITE is displayed")
        composeTestRule
            .onNodeWithText(ARTIST_FAVORITE)
            .performClick()
        Log.e("test $testNumber click", ARTIST_FAVORITE)

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(APP_BAR_TITLE)
                .text == ARTIST_FAVORITE
        }
        composeTestRule
            .onNodeWithTag(APP_BAR_TITLE)
            .assertTextEquals(ARTIST_FAVORITE)
        Log.e("test $testNumber assert", "app bar title is equals $ARTIST_FAVORITE")

        composeTestRule
            .onNodeWithTag(SONG_LIST_LAZY_COLUMN)
            .performScrollToIndex(index1)
        Log.e("test $testNumber scroll", "songList to index $index1")
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(song1.title)
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithText(song1.title)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${song1.title} is displayed")

        composeTestRule
            .onNodeWithTag(SONG_LIST_LAZY_COLUMN)
            .performScrollToIndex(index2)
        Log.e("test $testNumber scroll", "songList to index $index2")
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(song2.title)
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithText(song2.title)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${song2.title} is displayed")

        composeTestRule
            .onNodeWithTag(SONG_LIST_LAZY_COLUMN)
            .performScrollToIndex(index3)
        Log.e("test $testNumber scroll", "songList to index $index3")
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(song3.title)
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithText(song3.title)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${song3.title} is displayed")

        localRepository.setFavorite(false, ARTIST_1, TITLE_1_3)

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(TITLE_1_3)
                .isNotDisplayed()
        }
        composeTestRule
            .onNodeWithText(TITLE_1_3)
            .assertDoesNotExist()
        Log.e("test $testNumber assert", "$TITLE_1_3 does not exist")

        composeTestRule.pressBackUnconditionally()
        Log.e("test $testNumber action", "press back")

        composeTestRule.waitForCondition {
            composeTestRule.activityRule.scenario.state == Lifecycle.State.DESTROYED
        }
        assertEquals(composeTestRule.activityRule.scenario.state, Lifecycle.State.DESTROYED)
        Log.e("test $testNumber assert", "activity is destroyed")
    }

    @Test
    fun test1001_voiceCommandOpenArtistIsWorkingCorrectly() {
        val testNumber = 1001

        composeTestRule.activityRule.scenario.onActivity {
            parseAndExecuteVoiceCommand("открой группу $ARTIST_1")
        }

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(APP_BAR_TITLE)
                .isDisplayed()
        }

        val actualArtist = composeTestRule
            .onNodeWithTag(APP_BAR_TITLE)
            .text ?: "error"

        assertTrue(actualArtist.startEquallyWith(ARTIST_1))
        Log.e("test $testNumber assert", "app bar title starts equally with $ARTIST_1")

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

        val song1 = localRepository.getSongByArtistAndTitle(ARTIST_1, TITLE_1_1)
        assertNotNull(song1)

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText("$TITLE_1_1 ($ARTIST_1)")
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithText("$TITLE_1_1 ($ARTIST_1)")
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "song title with artist is displayed")
        composeTestRule
            .onNodeWithText(song1!!.text)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "song text is displayed correctly")
    }

    @Test
    fun test1003_voiceCommandOpenSongByArtistAndTitleIsWorkingCorrectly() {
        val testNumber = 1003

        composeTestRule.activityRule.scenario.onActivity {
            parseAndExecuteVoiceCommand("открой песню $ARTIST_3 $TITLE_3_1")
        }

        val song2 = localRepository.getSongByArtistAndTitle(ARTIST_3, TITLE_3_1)
        assertNotNull(song2)

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText("$TITLE_3_1 ($ARTIST_3)")
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithText("$TITLE_3_1 ($ARTIST_3)")
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "song title with artist is displayed")
        composeTestRule
            .onNodeWithText(song2!!.text)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "song text is displayed correctly")
    }

    @Test
    fun test1004_voiceCommandOpenSongByTitleWithDoubleBackPressingIsWorkingCorrectly_systemBack() {
        val testNumber = 1004

        composeTestRule.activityRule.scenario.onActivity {
            parseAndExecuteVoiceCommand("открой песню $TITLE_1_1")
        }

        val song1 = localRepository.getSongByArtistAndTitle(ARTIST_1, TITLE_1_1)
        assertNotNull(song1)

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText("$TITLE_1_1 ($ARTIST_1)")
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithText("$TITLE_1_1 ($ARTIST_1)")
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "song title with artist is displayed")
        composeTestRule
            .onNodeWithText(song1!!.text)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "song text is displayed correctly")

        composeTestRule.pressBackUnconditionally()
        Log.e("test $testNumber action", "press back")

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(APP_BAR_TITLE)
                .isDisplayed()
        }

        val actualArtist = composeTestRule
            .onNodeWithTag(APP_BAR_TITLE)
            .text ?: "error"

        assertTrue(actualArtist.startEquallyWith(ARTIST_1))
        Log.e("test $testNumber assert", "app bar title starts equally with $ARTIST_1")

        composeTestRule
            .onNodeWithText(song1.title)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "song title is displayed correctly")

        composeTestRule.pressBackUnconditionally()
        Log.e("test $testNumber action", "press back")

        composeTestRule.waitForCondition {
            composeTestRule.activityRule.scenario.state == Lifecycle.State.DESTROYED
        }
        assertEquals(composeTestRule.activityRule.scenario.state, Lifecycle.State.DESTROYED)
        Log.e("test $testNumber assert", "activity is destroyed")
    }

    @Test
    fun test1005_voiceCommandOpenSongByTitleWithDoubleBackPressingIsWorkingCorrectly_appBack() {
        val testNumber = 1005

        composeTestRule.activityRule.scenario.onActivity {
            parseAndExecuteVoiceCommand("открой песню $TITLE_1_1")
        }

        val song1 = localRepository.getSongByArtistAndTitle(ARTIST_1, TITLE_1_1)
        assertNotNull(song1)

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText("$TITLE_1_1 ($ARTIST_1)")
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithText("$TITLE_1_1 ($ARTIST_1)")
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "song title with artist is displayed")
        composeTestRule
            .onNodeWithText(song1!!.text)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "song text is displayed correctly")

        composeTestRule
            .onNodeWithTag(BACK_BUTTON)
            .performClick()
        Log.e("test $testNumber click", BACK_BUTTON)

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(APP_BAR_TITLE)
                .isDisplayed()
        }

        val actualArtist = composeTestRule
            .onNodeWithTag(APP_BAR_TITLE)
            .text ?: "error"

        assertTrue(actualArtist.startEquallyWith(ARTIST_1))
        Log.e("test $testNumber assert", "app bar title starts equally with $ARTIST_1")

        composeTestRule
            .onNodeWithText(song1.title)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "song title is displayed correctly")

        composeTestRule.pressBackUnconditionally()
        Log.e("test $testNumber action", "press back")

        composeTestRule.waitForCondition {
            composeTestRule.activityRule.scenario.state == Lifecycle.State.DESTROYED
        }
        assertEquals(composeTestRule.activityRule.scenario.state, Lifecycle.State.DESTROYED)
        Log.e("test $testNumber assert", "activity is destroyed")
    }

    @Test
    fun test1006_voiceCommandOpenSongByTitleFromFavoriteWithDoubleBackPressingIsWorkingCorrectly_systemBack() {
        val testNumber = 1006

        composeTestRule.activityRule.scenario.onActivity {
            parseAndExecuteVoiceCommand("открой раздел избранное")
        }

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(APP_BAR_TITLE)
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithTag(APP_BAR_TITLE)
            .assertTextEquals(ARTIST_FAVORITE)
        Log.e("test $testNumber assert", "app bar title is equals $ARTIST_FAVORITE")

        composeTestRule.activityRule.scenario.onActivity {
            parseAndExecuteVoiceCommand("открой песню $TITLE_1_1")
        }

        val song1 = localRepository.getSongByArtistAndTitle(ARTIST_1, TITLE_1_1)
        assertNotNull(song1)

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText("$TITLE_1_1 ($ARTIST_1)")
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithText("$TITLE_1_1 ($ARTIST_1)")
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "song title with artist is displayed")
        composeTestRule
            .onNodeWithText(song1!!.text)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "song text is displayed correctly")

        composeTestRule.pressBackUnconditionally()
        Log.e("test $testNumber action", "press back")

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(APP_BAR_TITLE)
                .isDisplayed()
        }

        val actualArtist = composeTestRule
            .onNodeWithTag(APP_BAR_TITLE)
            .text ?: "error"

        assertTrue(actualArtist.startEquallyWith(ARTIST_1))
        Log.e("test $testNumber assert", "app bar title starts equally with $ARTIST_1")

        composeTestRule
            .onNodeWithText(song1.title)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "song title is displayed correctly")

        composeTestRule.pressBackUnconditionally()
        Log.e("test $testNumber action", "press back")

        composeTestRule.waitForCondition {
            composeTestRule.activityRule.scenario.state == Lifecycle.State.DESTROYED
        }
        assertEquals(composeTestRule.activityRule.scenario.state, Lifecycle.State.DESTROYED)
        Log.e("test $testNumber assert", "activity is destroyed")
    }

    @Test
    fun test1007_voiceCommandOpenSongByTitleFromFavoriteWithDoubleBackPressingIsWorkingCorrectly_appBack() {
        val testNumber = 1007

        composeTestRule.activityRule.scenario.onActivity {
            parseAndExecuteVoiceCommand("открой раздел избранное")
        }

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(APP_BAR_TITLE)
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithTag(APP_BAR_TITLE)
            .assertTextEquals(ARTIST_FAVORITE)
        Log.e("test $testNumber assert", "app bar title is equals $ARTIST_FAVORITE")

        composeTestRule.activityRule.scenario.onActivity {
            parseAndExecuteVoiceCommand("открой песню $TITLE_1_1")
        }

        val song1 = localRepository.getSongByArtistAndTitle(ARTIST_1, TITLE_1_1)
        assertNotNull(song1)

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText("$TITLE_1_1 ($ARTIST_1)")
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithText("$TITLE_1_1 ($ARTIST_1)")
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "song title with artist is displayed")
        composeTestRule
            .onNodeWithText(song1!!.text)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "song text is displayed correctly")

        composeTestRule
            .onNodeWithTag(BACK_BUTTON)
            .performClick()
        Log.e("test $testNumber click", BACK_BUTTON)

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(APP_BAR_TITLE)
                .isDisplayed()
        }

        val actualArtist = composeTestRule
            .onNodeWithTag(APP_BAR_TITLE)
            .text ?: "error"

        assertTrue(actualArtist.startEquallyWith(ARTIST_1))
        Log.e("test $testNumber assert", "app bar title starts equally with $ARTIST_1")

        composeTestRule
            .onNodeWithText(song1.title)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "song title is displayed correctly")

        composeTestRule.pressBackUnconditionally()
        Log.e("test $testNumber action", "press back")

        composeTestRule.waitForCondition {
            composeTestRule.activityRule.scenario.state == Lifecycle.State.DESTROYED
        }
        assertEquals(composeTestRule.activityRule.scenario.state, Lifecycle.State.DESTROYED)
        Log.e("test $testNumber assert", "activity is destroyed")
    }

    @Test
    fun test1101_appIsClosingCorrectlyAfter3SongText_systemBack() {
        val testNumber = 1101

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(APP_BAR_TITLE)
                .isDisplayed()
        }

        val actualArtist = composeTestRule
            .onNodeWithTag(APP_BAR_TITLE)
            .text ?: "error"

        assertTrue(actualArtist.startEquallyWith(settingsRepository.defaultArtist))
        Log.e("test $testNumber assert", "app bar title starts equally with ${settingsRepository.defaultArtist}")

        val songs = localRepository
            .getSongsByArtistAsList(settingsRepository.defaultArtist)

        val firstSongIndex = 3

        val song1 = songs[firstSongIndex]
        val song2 = songs[firstSongIndex + 1]
        val song3 = songs[firstSongIndex + 2]

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(song1.title)
                .isDisplayed()
        }

        composeTestRule
            .onNodeWithText(song1.title)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${song1.title} is displayed")
        composeTestRule
            .onNodeWithText(song1.title)
            .performClick()
        Log.e("test $testNumber click", song1.title)
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText("${song1.title} (${song1.artist})")
                .isDisplayed()
        }

        composeTestRule
            .onNodeWithText("${song1.title} (${song1.artist})")
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "song title with artist is displayed")
        composeTestRule
            .onNodeWithText(song1.text)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "song text is displayed correctly")

        composeTestRule
            .onNodeWithTag(RIGHT_BUTTON)
            .performClick()
        Log.e("test $testNumber click", RIGHT_BUTTON)
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText("${song2.title} (${song2.artist})")
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithText("${song2.title} (${song2.artist})")
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "song2 title with artist is displayed")
        composeTestRule
            .onNodeWithText(song2.text)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "song text is displayed correctly")

        composeTestRule
            .onNodeWithTag(RIGHT_BUTTON)
            .performClick()
        Log.e("test $testNumber click", RIGHT_BUTTON)
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText("${song3.title} (${song3.artist})")
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithText("${song3.title} (${song3.artist})")
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "song3 title with artist is displayed")
        composeTestRule
            .onNodeWithText(song3.text)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "song text is displayed correctly")

        composeTestRule.pressBackUnconditionally()
        Log.e("test $testNumber action", "press back")

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(APP_BAR_TITLE)
                .isDisplayed()
        }

        val actualArtistAfterBackPressing = composeTestRule
            .onNodeWithTag(APP_BAR_TITLE)
            .text ?: "error"

        assertTrue(actualArtistAfterBackPressing.startEquallyWith(settingsRepository.defaultArtist))
        Log.e("test $testNumber assert", "app bar title starts equally with ${settingsRepository.defaultArtist}")

        composeTestRule
            .onNodeWithText(song3.title)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "song title is displayed correctly")

        composeTestRule.pressBackUnconditionally()
        Log.e("test $testNumber action", "press back")

        composeTestRule.waitForCondition {
            composeTestRule.activityRule.scenario.state == Lifecycle.State.DESTROYED
        }
        assertEquals(composeTestRule.activityRule.scenario.state, Lifecycle.State.DESTROYED)
        Log.e("test $testNumber assert", "activity is destroyed")
    }

    @Test
    fun test1102_appIsClosingCorrectlyAfter3SongText_appBack() {
        val testNumber = 1102

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(APP_BAR_TITLE)
                .isDisplayed()
        }

        val actualArtist = composeTestRule
            .onNodeWithTag(APP_BAR_TITLE)
            .text ?: "error"

        assertTrue(actualArtist.startEquallyWith(settingsRepository.defaultArtist))
        Log.e("test $testNumber assert", "app bar title starts equally with ${settingsRepository.defaultArtist}")

        val songs = localRepository
            .getSongsByArtistAsList(settingsRepository.defaultArtist)

        val firstSongIndex = 3

        val song1 = songs[firstSongIndex]
        val song2 = songs[firstSongIndex + 1]
        val song3 = songs[firstSongIndex + 2]

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(song1.title)
                .isDisplayed()
        }

        composeTestRule
            .onNodeWithText(song1.title)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${song1.title} is displayed")
        composeTestRule
            .onNodeWithText(song1.title)
            .performClick()
        Log.e("test $testNumber click", song1.title)
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText("${song1.title} (${song1.artist})")
                .isDisplayed()
        }

        composeTestRule
            .onNodeWithText("${song1.title} (${song1.artist})")
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "song title with artist is displayed")
        composeTestRule
            .onNodeWithText(song1.text)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "song text is displayed correctly")

        composeTestRule
            .onNodeWithTag(RIGHT_BUTTON)
            .performClick()
        Log.e("test $testNumber click", RIGHT_BUTTON)
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText("${song2.title} (${song2.artist})")
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithText("${song2.title} (${song2.artist})")
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "song2 title with artist is displayed")
        composeTestRule
            .onNodeWithText(song2.text)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "song text is displayed correctly")

        composeTestRule
            .onNodeWithTag(RIGHT_BUTTON)
            .performClick()
        Log.e("test $testNumber click", RIGHT_BUTTON)
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText("${song3.title} (${song3.artist})")
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithText("${song3.title} (${song3.artist})")
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "song3 title with artist is displayed")
        composeTestRule
            .onNodeWithText(song3.text)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "song text is displayed correctly")

        composeTestRule
            .onNodeWithTag(BACK_BUTTON)
            .performClick()
        Log.e("test $testNumber click", BACK_BUTTON)

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(APP_BAR_TITLE)
                .isDisplayed()
        }

        val actualArtistAfterBackPressing = composeTestRule
            .onNodeWithTag(APP_BAR_TITLE)
            .text ?: "error"

        assertTrue(actualArtistAfterBackPressing.startEquallyWith(settingsRepository.defaultArtist))
        Log.e("test $testNumber assert", "app bar title starts equally with ${settingsRepository.defaultArtist}")

        composeTestRule
            .onNodeWithText(song3.title)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "song title is displayed correctly")

        composeTestRule.pressBackUnconditionally()
        Log.e("test $testNumber action", "press back")

        composeTestRule.waitForCondition {
            composeTestRule.activityRule.scenario.state == Lifecycle.State.DESTROYED
        }
        assertEquals(composeTestRule.activityRule.scenario.state, Lifecycle.State.DESTROYED)
        Log.e("test $testNumber assert", "activity is destroyed")
    }

    @Test
    fun test1103_appIsClosingCorrectlyAfter3CloudSongText_systemBack() {
        val testNumber = 1103

        val list = cloudRepository.search("", CloudSearchOrderBy.BY_ID_DESC)

        val firstSongIndex = 3

        val cloudSong1 = list[firstSongIndex]
        val cloudSong2 = list[firstSongIndex + 1]
        val cloudSong3 = list[firstSongIndex + 2]

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(DRAWER_BUTTON_MAIN)
                .isDisplayed()
        }

        composeTestRule
            .onNodeWithTag(DRAWER_BUTTON_MAIN)
            .performClick()
        Log.e("test $testNumber click", DRAWER_BUTTON_MAIN)
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(ARTIST_CLOUD_SONGS)
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithText(ARTIST_CLOUD_SONGS)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$ARTIST_CLOUD_SONGS is displayed")
        composeTestRule
            .onNodeWithText(ARTIST_CLOUD_SONGS)
            .performClick()
        Log.e("test $testNumber click", ARTIST_CLOUD_SONGS)
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(CloudSearchOrderBy.BY_ID_DESC.orderByRus)
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithText(CloudSearchOrderBy.BY_ID_DESC.orderByRus)
            .assertIsDisplayed()
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(cloudSong1.visibleTitleWithRating)
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithText(cloudSong1.visibleTitleWithRating)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${cloudSong1.visibleTitleWithRating} is displayed")
        composeTestRule
            .onNodeWithText(cloudSong1.visibleTitleWithRating)
            .performClick()
        Log.e("test $testNumber click", cloudSong1.visibleTitleWithRating)
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(cloudSong1.visibleTitleWithArtistAndRating)
                .isDisplayed()
        }

        composeTestRule
            .onNodeWithText(cloudSong1.visibleTitleWithArtistAndRating)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "cloud song title with artist is displayed")
        composeTestRule
            .onNodeWithText(cloudSong1.text)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "cloud song text is displayed correctly")

        composeTestRule
            .onNodeWithTag(RIGHT_BUTTON)
            .performClick()
        Log.e("test $testNumber click", RIGHT_BUTTON)
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(cloudSong2.visibleTitleWithArtistAndRating)
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithText(cloudSong2.visibleTitleWithArtistAndRating)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "cloud song 2 title with artist is displayed")
        composeTestRule
            .onNodeWithText(cloudSong2.text)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "cloud song 2 text is displayed correctly")

        composeTestRule
            .onNodeWithTag(RIGHT_BUTTON)
            .performClick()
        Log.e("test $testNumber click", RIGHT_BUTTON)
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(cloudSong3.visibleTitleWithArtistAndRating)
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithText(cloudSong3.visibleTitleWithArtistAndRating)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "cloud song 3 title with artist is displayed")
        composeTestRule
            .onNodeWithText(cloudSong3.text)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "cloud song 2 text is displayed correctly")

        composeTestRule.pressBackUnconditionally()
        Log.e("test $testNumber action", "press back")

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(APP_BAR_TITLE)
                .isDisplayed()
        }

        val actualArtistAfterBackPressing = composeTestRule
            .onNodeWithTag(APP_BAR_TITLE)
            .text ?: "error"

        assertTrue(actualArtistAfterBackPressing.startEquallyWith(ARTIST_CLOUD_SONGS))
        Log.e("test $testNumber assert", "app bar title starts equally with $ARTIST_CLOUD_SONGS")

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(cloudSong3.visibleTitleWithRating)
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithText(cloudSong3.visibleTitleWithRating)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "song title is displayed correctly")

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(TEXT_FIELD_SEARCH_FOR)
                .isDisplayed()
        }

        composeTestRule
            .onNodeWithTag(TEXT_FIELD_SEARCH_FOR)
            .performTextReplacement("Ло")
        Log.e("test $testNumber input", "Ло")
        Espresso.closeSoftKeyboard()
        composeTestRule.waitForTimeout(timeout)

        composeTestRule.pressBackUnconditionally()
        Log.e("test $testNumber action", "press back")

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(APP_BAR_TITLE)
                .isDisplayed()
        }

        val actualArtistAfterSecondBackPressing = composeTestRule
            .onNodeWithTag(APP_BAR_TITLE)
            .text ?: "error"

        assertTrue(actualArtistAfterSecondBackPressing.startEquallyWith(settingsRepository.defaultArtist))
        Log.e("test $testNumber assert", "app bar title starts equally with ${settingsRepository.defaultArtist}")

        composeTestRule.pressBackUnconditionally()
        Log.e("test $testNumber action", "press back")

        composeTestRule.waitForCondition {
            composeTestRule.activityRule.scenario.state == Lifecycle.State.DESTROYED
        }
        assertEquals(composeTestRule.activityRule.scenario.state, Lifecycle.State.DESTROYED)
        Log.e("test $testNumber assert", "activity is destroyed")
    }

    @Test
    fun test1104_appIsClosingCorrectlyAfter3CloudSongText_appBack() {
        val testNumber = 1104

        val list = cloudRepository.search("", CloudSearchOrderBy.BY_ID_DESC)

        val firstSongIndex = 3

        val cloudSong1 = list[firstSongIndex]
        val cloudSong2 = list[firstSongIndex + 1]
        val cloudSong3 = list[firstSongIndex + 2]

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(DRAWER_BUTTON_MAIN)
                .isDisplayed()
        }

        composeTestRule
            .onNodeWithTag(DRAWER_BUTTON_MAIN)
            .performClick()
        Log.e("test $testNumber click", DRAWER_BUTTON_MAIN)
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(ARTIST_CLOUD_SONGS)
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithText(ARTIST_CLOUD_SONGS)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$ARTIST_CLOUD_SONGS is displayed")
        composeTestRule
            .onNodeWithText(ARTIST_CLOUD_SONGS)
            .performClick()
        Log.e("test $testNumber click", ARTIST_CLOUD_SONGS)
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(CloudSearchOrderBy.BY_ID_DESC.orderByRus)
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithText(CloudSearchOrderBy.BY_ID_DESC.orderByRus)
            .assertIsDisplayed()
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(cloudSong1.visibleTitleWithRating)
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithText(cloudSong1.visibleTitleWithRating)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${cloudSong1.visibleTitleWithRating} is displayed")
        composeTestRule
            .onNodeWithText(cloudSong1.visibleTitleWithRating)
            .performClick()
        Log.e("test $testNumber click", cloudSong1.visibleTitleWithRating)
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(cloudSong1.visibleTitleWithArtistAndRating)
                .isDisplayed()
        }

        composeTestRule
            .onNodeWithText(cloudSong1.visibleTitleWithArtistAndRating)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "cloud song title with artist is displayed")
        composeTestRule
            .onNodeWithText(cloudSong1.text)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "cloud song text is displayed correctly")

        composeTestRule
            .onNodeWithTag(RIGHT_BUTTON)
            .performClick()
        Log.e("test $testNumber click", RIGHT_BUTTON)
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(cloudSong2.visibleTitleWithArtistAndRating)
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithText(cloudSong2.visibleTitleWithArtistAndRating)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "cloud song 2 title with artist is displayed")
        composeTestRule
            .onNodeWithText(cloudSong2.text)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "cloud song 2 text is displayed correctly")

        composeTestRule
            .onNodeWithTag(RIGHT_BUTTON)
            .performClick()
        Log.e("test $testNumber click", RIGHT_BUTTON)
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(cloudSong3.visibleTitleWithArtistAndRating)
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithText(cloudSong3.visibleTitleWithArtistAndRating)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "cloud song 3 title with artist is displayed")
        composeTestRule
            .onNodeWithText(cloudSong3.text)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "cloud song 2 text is displayed correctly")

        composeTestRule.pressBackUnconditionally()
        Log.e("test $testNumber action", "press back")

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(APP_BAR_TITLE)
                .isDisplayed()
        }

        val actualArtistAfterBackPressing = composeTestRule
            .onNodeWithTag(APP_BAR_TITLE)
            .text ?: "error"

        assertTrue(actualArtistAfterBackPressing.startEquallyWith(ARTIST_CLOUD_SONGS))
        Log.e("test $testNumber assert", "app bar title starts equally with $ARTIST_CLOUD_SONGS")

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(cloudSong3.visibleTitleWithRating)
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithText(cloudSong3.visibleTitleWithRating)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "song title is displayed correctly")

        composeTestRule
            .onNodeWithTag(BACK_BUTTON)
            .performClick()
        Log.e("test $testNumber click", BACK_BUTTON)

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(APP_BAR_TITLE)
                .isDisplayed()
        }

        val actualArtistAfterSecondBackPressing = composeTestRule
            .onNodeWithTag(APP_BAR_TITLE)
            .text ?: "error"

        assertTrue(actualArtistAfterSecondBackPressing.startEquallyWith(settingsRepository.defaultArtist))
        Log.e("test $testNumber assert", "app bar title starts equally with ${settingsRepository.defaultArtist}")

        composeTestRule.pressBackUnconditionally()
        Log.e("test $testNumber action", "press back")

        composeTestRule.waitForCondition {
            composeTestRule.activityRule.scenario.state == Lifecycle.State.DESTROYED
        }
        assertEquals(composeTestRule.activityRule.scenario.state, Lifecycle.State.DESTROYED)
        Log.e("test $testNumber assert", "activity is destroyed")
    }

    @Test
    fun test1201_textSearchListIsOpeningFromMenuCorrectly() {
        val testNumber = 1201

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(DRAWER_BUTTON_MAIN)
                .isDisplayed()
        }

        composeTestRule
            .onNodeWithTag(DRAWER_BUTTON_MAIN)
            .performClick()
        Log.e("test $testNumber click", DRAWER_BUTTON_MAIN)
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(ARTIST_TEXT_SEARCH)
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithText(ARTIST_TEXT_SEARCH)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "$ARTIST_CLOUD_SONGS is displayed")
        composeTestRule
            .onNodeWithText(ARTIST_TEXT_SEARCH)
            .performClick()
        Log.e("test $testNumber click", ARTIST_CLOUD_SONGS)
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(TextSearchOrderBy.BY_TITLE.orderByRus)
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithText(TextSearchOrderBy.BY_TITLE.orderByRus)
            .assertIsDisplayed()
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(stringConst.listIsEmpty)
                .isDisplayed()
        }
        Log.e("test $testNumber assert", "${stringConst.listIsEmpty} is displayed")
    }

    @Test
    fun test1202_textSearchListNormalQueryIsWorkingCorrectly() {
        val testNumber = 1202

        composeTestRule.activityRule.scenario.onActivity {
            CommonViewModel
                .getStoredInstance()
                ?.submitAction(SelectScreen(ScreenVariant.TextSearchList()))
        }

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(TEXT_FIELD_SEARCH_FOR)
                .isDisplayed()
        }

        composeTestRule
            .onNodeWithTag(TEXT_FIELD_SEARCH_FOR)
            .performTextReplacement("сердца")
        Log.e("test $testNumber input", "сердца")
        Espresso.closeSoftKeyboard()
        composeTestRule.waitForTimeout(timeout)
        composeTestRule
            .onNodeWithTag(SEARCH_BUTTON)
            .performClick()
        Log.e("test $testNumber click", SEARCH_BUTTON)

        val list = localRepository.getSongsByTextSearch(listOf("сердца"), TextSearchOrderBy.BY_TITLE)

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(list[0].title)
                .isDisplayed()
        }

        composeTestRule
            .onNodeWithText(list[0].title)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${list[0].title} is displayed")
        composeTestRule
            .onNodeWithText(list[0].artist)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${list[0].artist} is displayed")

        composeTestRule
            .onNodeWithText(list[1].title)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${list[1].title} is displayed")
        composeTestRule
            .onNodeWithText(list[1].artist)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${list[1].artist} is displayed")

        composeTestRule
            .onNodeWithText(list[2].title)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${list[2].title} is displayed")
        composeTestRule
            .onNodeWithText(list[2].artist)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${list[2].artist} is displayed")
    }

    @Test
    fun test1203_textSearchListOrderingByArtistIsWorkingCorrectly() {
        val testNumber = 1203

        composeTestRule.activityRule.scenario.onActivity {
            CommonViewModel
                .getStoredInstance()
                ?.submitAction(SelectScreen(ScreenVariant.TextSearchList()))
        }

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(TEXT_FIELD_SEARCH_FOR)
                .isDisplayed()
        }

        composeTestRule
            .onNodeWithTag(TEXT_FIELD_SEARCH_FOR)
            .performTextReplacement("сердца")
        Log.e("test $testNumber input", "сердца")
        Espresso.closeSoftKeyboard()
        composeTestRule.waitForTimeout(timeout)
        composeTestRule
            .onNodeWithTag(SEARCH_BUTTON)
            .performClick()
        Log.e("test $testNumber click", SEARCH_BUTTON)

        composeTestRule
            .onNodeWithText(TextSearchOrderBy.BY_TITLE.orderByRus)
            .performClick()
        Log.e("test $testNumber click", TextSearchOrderBy.BY_TITLE.orderByRus)
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(TextSearchOrderBy.BY_ARTIST.orderByRus)
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithText(TextSearchOrderBy.BY_ARTIST.orderByRus)
            .performClick()
        Log.e("test $testNumber click", TextSearchOrderBy.BY_ARTIST.orderByRus)

        val list = localRepository.getSongsByTextSearch(listOf("сердца"), TextSearchOrderBy.BY_ARTIST)

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(list[0].title)
                .isDisplayed()
        }

        composeTestRule
            .onNodeWithText(list[0].title)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${list[0].title} is displayed")
        composeTestRule
            .onAllNodesWithText(list[0].artist)
            .onFirst()
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${list[0].artist} is displayed")

        composeTestRule
            .onNodeWithText(list[1].title)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${list[1].title} is displayed")
        composeTestRule
            .onAllNodesWithText(list[1].artist)
            .onFirst()
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${list[1].artist} is displayed")

        composeTestRule
            .onNodeWithText(list[2].title)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${list[2].title} is displayed")
        composeTestRule
            .onAllNodesWithText(list[2].artist)
            .onFirst()
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${list[2].artist} is displayed")
    }

    @Test
    fun test1204_textSearchListQueryWithEmptyResultIsWorkingCorrectly() {
        val testNumber = 1204

        composeTestRule.activityRule.scenario.onActivity {
            CommonViewModel
                .getStoredInstance()
                ?.submitAction(SelectScreen(ScreenVariant.TextSearchList()))
        }

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(TEXT_FIELD_SEARCH_FOR)
                .isDisplayed()
        }

        composeTestRule
            .onNodeWithTag(TEXT_FIELD_SEARCH_FOR)
            .performTextReplacement("сердца")
        Log.e("test $testNumber input", "сердца")
        Espresso.closeSoftKeyboard()
        composeTestRule.waitForTimeout(timeout)
        composeTestRule
            .onNodeWithTag(SEARCH_BUTTON)
            .performClick()
        Log.e("test $testNumber click", SEARCH_BUTTON)
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(stringConst.listIsEmpty)
                .isNotDisplayed()
        }

        composeTestRule
            .onNodeWithTag(TEXT_FIELD_SEARCH_FOR)
            .performTextReplacement("Ппщдылдж")
        Log.e("test $testNumber input", "Ппщдылдж")
        Espresso.closeSoftKeyboard()
        composeTestRule.waitForTimeout(timeout)
        composeTestRule
            .onNodeWithTag(SEARCH_BUTTON)
            .performClick()
        Log.e("test $testNumber click", SEARCH_BUTTON)
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(stringConst.listIsEmpty)
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithText(stringConst.listIsEmpty)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${stringConst.listIsEmpty} is displayed")
    }

    @Test
    fun test1301_textSearchSongTextIsOpeningFromTextSearchListCorrectly() {
        val testNumber = 1301

        composeTestRule.activityRule.scenario.onActivity {
            CommonViewModel
                .getStoredInstance()
                ?.submitAction(SelectScreen(ScreenVariant.TextSearchList()))
        }

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(TEXT_FIELD_SEARCH_FOR)
                .isDisplayed()
        }

        composeTestRule
            .onNodeWithTag(TEXT_FIELD_SEARCH_FOR)
            .performTextReplacement("сердца")
        Log.e("test $testNumber input", "сердца")
        Espresso.closeSoftKeyboard()
        composeTestRule.waitForTimeout(timeout)
        composeTestRule
            .onNodeWithTag(SEARCH_BUTTON)
            .performClick()
        Log.e("test $testNumber click", SEARCH_BUTTON)

        val list = localRepository.getSongsByTextSearch(listOf("сердца"), TextSearchOrderBy.BY_TITLE)

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(list[2].title)
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithText(list[2].title)
            .performClick()
        Log.e("test $testNumber click", list[2].title)
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText("${list[2].title} (${list[2].artist})")
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithText("${list[2].title} (${list[2].artist})")
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "${list[2].title} (${list[2].artist})")
        composeTestRule
            .onNodeWithText(list[2].text)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "song text is displayed correctly")
    }

    @Test
    fun test1302_textSearchSongTextLeftAndRightButtonsAreWorkingCorrectly() {
        val testNumber = 1302

        composeTestRule.activityRule.scenario.onActivity {
            CommonViewModel
                .getStoredInstance()
                ?.submitAction(SelectScreen(ScreenVariant.TextSearchList()))
        }

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithTag(TEXT_FIELD_SEARCH_FOR)
                .isDisplayed()
        }

        composeTestRule
            .onNodeWithTag(TEXT_FIELD_SEARCH_FOR)
            .performTextReplacement("сердца")
        Log.e("test $testNumber input", "сердца")
        Espresso.closeSoftKeyboard()
        composeTestRule.waitForTimeout(timeout)
        composeTestRule
            .onNodeWithTag(SEARCH_BUTTON)
            .performClick()
        Log.e("test $testNumber click", SEARCH_BUTTON)

        val list = localRepository.getSongsByTextSearch(listOf("сердца"), TextSearchOrderBy.BY_TITLE)

        val songIndex1 = 3
        val song1 = list[songIndex1]
        val song2 = list[songIndex1 + 1]

        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText(song1.title)
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithText(song1.title)
            .performClick()
        Log.e("test $testNumber click", song1.title)
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText("${song1.title} (${song1.artist})")
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithText("${song1.title} (${song1.artist})")
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "song1 title with artist is displayed")
        composeTestRule
            .onNodeWithText(song1.text)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "song text is displayed correctly")

        composeTestRule
            .onNodeWithTag(RIGHT_BUTTON)
            .performClick()
        Log.e("test $testNumber click", RIGHT_BUTTON)
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText("${song2.title} (${song2.artist})")
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithText("${song2.title} (${song2.artist})")
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "song2 title with artist is displayed")
        composeTestRule
            .onNodeWithText(song2.text)
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "song text is displayed correctly")

        composeTestRule
            .onNodeWithTag(LEFT_BUTTON)
            .performClick()
        Log.e("test $testNumber click", LEFT_BUTTON)
        composeTestRule.waitForCondition {
            composeTestRule
                .onNodeWithText("${song1.title} (${song1.artist})")
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithText("${song1.title} (${song1.artist})")
            .assertIsDisplayed()
        Log.e("test $testNumber assert", "song1 title with artist is displayed")
    }
}

const val timeout = 1500L

val SemanticsNodeInteraction.text: String?
    get() = (this
        .fetchSemanticsNode()
        .config
        .getOrNull(SemanticsProperties.Text)
        ?.boxedValue as? List<*>)
        ?.getOrNull(0)?.toString()

fun String.startEquallyWith(with: String, len: Int = 10): Boolean {
    val startOfThis = this.take(len)
    val startOfWith = with.take(len)
    return startOfThis == startOfWith
}

fun AndroidComposeTestRule<out TestRule, out ComponentActivity>.waitForTimeout(time: Long) {
    try {
        waitUntil(time) { false }
    } catch (e: ComposeTimeoutException) {
        Log.e("test wait", "timeout")
    }
}

fun AndroidComposeTestRule<out TestRule, out ComponentActivity>.waitForCondition(
    condition: () -> Boolean
) {
    try {
        waitUntil(10000L) { condition() }
    } catch (e: ComposeTimeoutException) {
        Log.e("test wait", "timeout")
    }
}

fun AndroidComposeTestRule<out TestRule, out ComponentActivity>.pressBackUnconditionally() {
    Espresso.closeSoftKeyboard()
    waitForTimeout(timeout)
    Espresso.pressBackUnconditionally()
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
    val titleSettings = context.getString(R.string.title_settings)
    val theme = context.getString(R.string.theme)
    val fontScale = context.getString(R.string.font_scale)
    val defArtist = context.getString(R.string.def_artist)
    val orientFix = context.getString(R.string.orient_fix)
    val listenToMusic = context.getString(R.string.listen_to_music)
    val scrollSpeed = context.getString(R.string.scroll_speed)
    val saveAndRestart = context.getString(R.string.apply_settings)
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
