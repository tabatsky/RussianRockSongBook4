package jatx.russianrocksongbook.preferences

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.compose.ui.graphics.Color
import kotlin.math.pow

const val PREFS_NAME = "RussianRockPreferences"

const val KEY_APK_VERSION = "apkVersion"
const val KEY_THEME = "theme_int"
const val KEY_ORIENTATION = "orientation_int"
const val KEY_DEFAULT_ARTIST = "defaultArtist"
const val KEY_FONT_SCALE = "fontScale"
const val KEY_FOOTER_ROWS = "footerRows"
const val KEY_SCROLL_SPEED = "scrollSpeed"
const val KEY_YOUTUBE_MUSIC_DONT_ASK = "youtubeMusicDontAsk"
const val KEY_VK_MUSIC_DONT_ASK = "vkMusicDontAsk"

const val ARTIST_KINO = "Кино"

@SuppressLint("ApplySharedPref")
class Settings(
    private val context: Context
) {
    private val sp = context.getSharedPreferences(PREFS_NAME, 0)

    val appWasUpdated: Boolean
        get() {
            return try {
                val pInfo =
                    context.packageManager.getPackageInfo(context.packageName, 0)
                val newVersion = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    pInfo.longVersionCode.toInt()
                } else {
                    pInfo.versionCode
                }

                val oldVersion = sp.getInt(KEY_APK_VERSION, 0)

                (newVersion > oldVersion)
            } catch (e: Throwable) {
                false
            }
        }

    fun confirmAppUpdate() {
        try {
            val pInfo =
                context.packageManager.getPackageInfo(context.packageName, 0)
            val newVersion = pInfo.versionCode

            val editor = sp.edit()
            editor.putInt(KEY_APK_VERSION, newVersion)
            editor.commit()
        } catch (e: Throwable) {}
    }

    var theme: Theme
        get() = Theme.values()[sp.getInt(
            KEY_THEME, 0)]
        set(value) {
            val editor = sp.edit()
            editor.putInt(KEY_THEME, value.ordinal)
            editor.commit()
        }

    var orientation: Orientation
        get() = Orientation.values()[sp.getInt(KEY_ORIENTATION, 0)]
        set(value) {
            val editor = sp.edit()
            editor.putInt(KEY_ORIENTATION, value.ordinal)
            editor.commit()
        }

    var defaultArtist: String
        get() = sp.getString(KEY_DEFAULT_ARTIST, null) ?: ARTIST_KINO
        set(value) {
            val editor = sp.edit()
            editor.putString(KEY_DEFAULT_ARTIST, value)
            editor.commit()
        }

    var footerRows: Int
        get() = sp.getInt(KEY_FOOTER_ROWS, 1)
        set(value) {
            val editor = sp.edit()
            editor.putInt(KEY_FOOTER_ROWS, value)
            editor.commit()
        }

    var scrollSpeed: Float
        get() = sp.getFloat(KEY_SCROLL_SPEED, 1.0f)
        set(value) {
            val editor = sp.edit()
            editor.putFloat(KEY_SCROLL_SPEED, value)
            editor.commit()
        }

    var youtubeMusicDontAsk: Boolean
        get() = sp.getBoolean(KEY_YOUTUBE_MUSIC_DONT_ASK, false)
        set(value) {
            val editor = sp.edit()
            editor.putBoolean(KEY_YOUTUBE_MUSIC_DONT_ASK, value)
            editor.commit()
        }

    var vkMusicDontAsk: Boolean
        get() = sp.getBoolean(KEY_VK_MUSIC_DONT_ASK, false)
        set(value) {
            val editor = sp.edit()
            editor.putBoolean(KEY_VK_MUSIC_DONT_ASK, value)
            editor.commit()
        }
    
    var commonFontScale: Float
        get() = sp.getFloat(KEY_FONT_SCALE, 1.0f)
        set(value) {
            val editor = sp.edit()
            editor.putFloat(KEY_FONT_SCALE, value)
            editor.commit()
        }

    val commonFontScaleEnum: FontScale
        get() = FontScale.values().find { it.scale == commonFontScale } ?: FontScale.M

    fun getSpecificFontScale(scalePow: ScalePow) = commonFontScale.pow(scalePow.pow)
}

enum class Theme(
    val colorMain: Color,
    val colorBg: Color,
    val colorCommon: Color
) {
    DARK(
        Color(0xFFFFFFBB),
        Color(0xFF000000),
        Color(0xFF777755)
    ), LIGHT(
        Color(0xFF000000),
        Color(0xFFFFFFBB),
        Color(0xFF777755)
    )
}

enum class Orientation {
    RANDOM, PORTRAIT, LANDSCAPE
}

enum class ScalePow(
    val pow: Float
) {
    TEXT(1.0f),
    LABEL(0.5f),
    MENU(0.7f),
    BUTTON(0.3f)
}

enum class FontScale(
    val scale: Float
) {
    XS(0.5f), S(0.75f), M(1.0f), L(1.5f), XL(2.0f)
}
