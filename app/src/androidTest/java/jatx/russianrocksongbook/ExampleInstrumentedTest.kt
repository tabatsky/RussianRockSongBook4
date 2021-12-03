package jatx.russianrocksongbook

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import jatx.russianrocksongbook.view.CurrentScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test


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

    @Before
    fun init() {
        hiltRule.inject()
        composeTestRule.setContent {
            CurrentScreen()
        }
    }

    @Test
    fun menuAndSongListTest() {
        composeTestRule.onNodeWithTag("drawerButtonMain").performClick()
        Log.e("test click", "drawerButtonMain")
        composeTestRule.onNodeWithText("Избранное").assertIsDisplayed()
        Log.e("test assert", "Избранное is displayed")
        composeTestRule.onNodeWithText("Добавить исполнителя").assertIsDisplayed()
        Log.e("test assert", "Добавить исполнителя is displayed")
        composeTestRule.onNodeWithText("Добавить песню").assertIsDisplayed()
        Log.e("test assert", "Добавить песню is displayed")
        composeTestRule.onNodeWithText("Аккорды онлайн").assertIsDisplayed()
        Log.e("test assert", "Аккорды онлайн is displayed")
        composeTestRule.onNodeWithText("Пожертвования").assertIsDisplayed()
        Log.e("test assert", "Пожертвования is displayed")
        composeTestRule.onNodeWithText("Немного Нервно").assertDoesNotExist()
        Log.e("test assert", "Немного Нервно does not exist")
        composeTestRule.onNodeWithTag("menuLazyColumn").performScrollToIndex(30)
        Log.e("test scroll", "menu to index 30")
        composeTestRule.onNodeWithText("Евгений Александрович").assertIsDisplayed()
        Log.e("test assert", "Евгений Александрович is displayed")
        composeTestRule.onNodeWithTag("menuLazyColumn").performScrollToIndex(40)
        Log.e("test scroll", "menu to index 40")
        composeTestRule.onNodeWithText("Немного Нервно").assertIsDisplayed()
        Log.e("test assert", "Немного Нервно is displayed")
        composeTestRule.onNodeWithText("Немного Нервно").performClick()
        composeTestRule.onNodeWithText("Santa Maria").assertIsDisplayed()
        Log.e("test assert", "Santa Maria is displayed")
        composeTestRule.onNodeWithText("Яблочный остров").assertDoesNotExist()
        Log.e("test assert", "Яблочный остров does not exist")
    }
}