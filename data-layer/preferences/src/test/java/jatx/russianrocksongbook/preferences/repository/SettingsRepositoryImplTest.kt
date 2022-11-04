package jatx.russianrocksongbook.preferences.repository

import android.content.Context
import android.content.SharedPreferences
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verifySequence
import jatx.russianrocksongbook.domain.repository.preferences.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters
import kotlin.math.pow

@MockKExtension.ConfirmVerification
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class SettingsRepositoryImplTest {
    lateinit var context: Context

    lateinit var sp: SharedPreferences

    lateinit var editor: SharedPreferences.Editor

    lateinit var settingsRepository: SettingsRepository

    @Before
    fun init() {
        context = mockk(relaxed = true)
        sp = mockk(relaxed = true)
        editor = mockk(relaxed = true)

        every { context.getSharedPreferences(any(), any()) } returns sp
        every { sp.edit() } returns editor

        settingsRepository = SettingsRepositoryImpl(context)

        verifySequence {
            context.getSharedPreferences(PREFS_NAME, 0)
        }
    }

    @Test
    fun test001_getTheme_isWorkingCorrect() {
        every { sp.getInt(any(), any()) } returns 0

        val theme = settingsRepository.theme

        assertEquals(Theme.values()[0], theme)

        verifySequence {
            sp.getInt(KEY_THEME, 0)
        }
    }

    @Test
    fun test002_setTheme_isWorkingCorrect() {
        settingsRepository.theme = Theme.LIGHT

        verifySequence {
            sp.edit()
            editor.putInt(KEY_THEME, Theme.LIGHT.ordinal)
            editor.commit()
        }
    }

    @Test
    fun test003_getOrientation_isWorkingCorrect() {
        every { sp.getInt(any(), any()) } returns 0

        val orientation = settingsRepository.orientation

        assertEquals(Orientation.values()[0], orientation)

        verifySequence {
            sp.getInt(KEY_ORIENTATION, 0)
        }
    }

    @Test
    fun test004_setOrientation_isWorkingCorrect() {
        settingsRepository.orientation = Orientation.LANDSCAPE

        verifySequence {
            sp.edit()
            editor.putInt(KEY_ORIENTATION, Orientation.LANDSCAPE.ordinal)
            editor.commit()
        }
    }

    @Test
    fun test005_getListenToMusicVariant_isWorkingCorrect() {
        every { sp.getInt(any(), any()) } returns 0

        val listenToMusicVariant = settingsRepository.listenToMusicVariant

        assertEquals(ListenToMusicVariant.values()[0], listenToMusicVariant)

        verifySequence {
            sp.getInt(KEY_LISTEN_TO_MUSIC_VARIANT, 1)
        }
    }

    @Test
    fun test006_setListenToMusicVariant_isWorkingCorrect() {
        settingsRepository.listenToMusicVariant = ListenToMusicVariant.VK_AND_YOUTUBE

        verifySequence {
            sp.edit()
            editor.putInt(KEY_LISTEN_TO_MUSIC_VARIANT, ListenToMusicVariant.VK_AND_YOUTUBE.ordinal)
            editor.commit()
        }
    }

    @Test
    fun test007_getDefaultArtist_isWorkingCorrect() {
        every { sp.getString(any(), any()) } returns "Сплин"

        val defaultArtist = settingsRepository.defaultArtist

        assertEquals("Сплин", defaultArtist)

        verifySequence {
            sp.getString(KEY_DEFAULT_ARTIST, null)
        }
    }

    @Test
    fun test008_setTheme_isWorkingCorrect() {
        settingsRepository.defaultArtist = "Сплин"

        verifySequence {
            sp.edit()
            editor.putString(KEY_DEFAULT_ARTIST, "Сплин")
            editor.commit()
        }
    }

    @Test
    fun test009_getScrollSpeed_isWorkingCorrect() {
        every { sp.getFloat(any(), any()) } returns 0.37f

        val scrollSpeed = settingsRepository.scrollSpeed

        assertEquals(0.37f, scrollSpeed)

        verifySequence {
            sp.getFloat(KEY_SCROLL_SPEED, 1.0f)
        }
    }

    @Test
    fun test010_setScrollSpeed_isWorkingCorrect() {
        settingsRepository.scrollSpeed = 1.2f

        verifySequence {
            sp.edit()
            editor.putFloat(KEY_SCROLL_SPEED, 1.2f)
            editor.commit()
        }
    }

    @Test
    fun test011_getYoutubeMusicDontAsk_isWorkingCorrect() {
        every { sp.getBoolean(any(), any()) } returns true

        val dontAsk = settingsRepository.youtubeMusicDontAsk

        assertEquals(true, dontAsk)

        verifySequence {
            sp.getBoolean(KEY_YOUTUBE_MUSIC_DONT_ASK, false)
        }
    }

    @Test
    fun test012_setYoutubeMusicDontAsk_isWorkingCorrect() {
        settingsRepository.youtubeMusicDontAsk = true

        verifySequence {
            sp.edit()
            editor.putBoolean(KEY_YOUTUBE_MUSIC_DONT_ASK, true)
            editor.commit()
        }
    }

    @Test
    fun test013_getVkMusicDontAsk_isWorkingCorrect() {
        every { sp.getBoolean(any(), any()) } returns true

        val dontAsk = settingsRepository.vkMusicDontAsk

        assertEquals(true, dontAsk)

        verifySequence {
            sp.getBoolean(KEY_VK_MUSIC_DONT_ASK, false)
        }
    }

    @Test
    fun test014_setVkMusicDontAsk_isWorkingCorrect() {
        settingsRepository.vkMusicDontAsk = true

        verifySequence {
            sp.edit()
            editor.putBoolean(KEY_VK_MUSIC_DONT_ASK, true)
            editor.commit()
        }
    }

    @Test
    fun test015_getYandexMusicDontAsk_isWorkingCorrect() {
        every { sp.getBoolean(any(), any()) } returns true

        val dontAsk = settingsRepository.yandexMusicDontAsk

        assertEquals(true, dontAsk)

        verifySequence {
            sp.getBoolean(KEY_YANDEX_MUSIC_DONT_ASK, false)
        }
    }

    @Test
    fun test016_setYoutubeMusicDontAsk_isWorkingCorrect() {
        settingsRepository.yandexMusicDontAsk = true

        verifySequence {
            sp.edit()
            editor.putBoolean(KEY_YANDEX_MUSIC_DONT_ASK, true)
            editor.commit()
        }
    }

    @Test
    fun test017_getVoiceHelpDontAsk_isWorkingCorrect() {
        every { sp.getBoolean(any(), any()) } returns true

        val dontAsk = settingsRepository.voiceHelpDontAsk

        assertEquals(true, dontAsk)

        verifySequence {
            sp.getBoolean(KEY_VOICE_HELP_DONT_ASK, false)
        }
    }

    @Test
    fun test018_setVoiceHelpDontAsk_isWorkingCorrect() {
        settingsRepository.voiceHelpDontAsk = true

        verifySequence {
            sp.edit()
            editor.putBoolean(KEY_VOICE_HELP_DONT_ASK, true)
            editor.commit()
        }
    }

    @Test
    fun test019_getCommonFontScale_isWorkingCorrect() {
        every { sp.getFloat(any(), any()) } returns FontScale.M.scale

        val fontScale = settingsRepository.commonFontScale
        val fontScaleEnum = settingsRepository.commonFontScaleEnum

        assertEquals(FontScale.M.scale, fontScale)
        assertEquals(FontScale.M, fontScaleEnum)

        verifySequence {
            sp.getFloat(KEY_FONT_SCALE, 1.0f)
            sp.getFloat(KEY_FONT_SCALE, 1.0f)
        }
    }

    @Test
    fun test020_setCommonFontScale_isWorkingCorrect() {
        settingsRepository.commonFontScale = FontScale.S.scale

        verifySequence {
            sp.edit()
            editor.putFloat(KEY_FONT_SCALE, FontScale.S.scale)
            editor.commit()
        }
    }

    @Test
    fun test021_getSpecificFontScale_isWorkingCorrect() {
        every { sp.getFloat(any(), any()) } returns FontScale.S.scale

        val scale = settingsRepository.getSpecificFontScale(ScalePow.MENU)

        assertEquals(FontScale.S.scale.pow(ScalePow.MENU.pow), scale)

        verifySequence {
            sp.getFloat(KEY_FONT_SCALE, 1.0f)
        }
    }

}