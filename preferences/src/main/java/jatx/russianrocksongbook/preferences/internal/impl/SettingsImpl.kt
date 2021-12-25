package jatx.russianrocksongbook.preferences.internal.impl

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.compose.ui.graphics.Color
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import it.czerwinski.android.hilt.annotations.BoundTo
import jatx.russianrocksongbook.preferences.api.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.pow

private const val PREFS_NAME = "RussianRockPreferences"

private const val KEY_APK_VERSION = "apkVersion"
private const val KEY_THEME = "theme_int"
private const val KEY_ORIENTATION = "orientation_int"
private const val KEY_DEFAULT_ARTIST = "defaultArtist"
private const val KEY_FONT_SCALE = "fontScale"
private const val KEY_LISTEN_TO_MUSIC_VARIANT = "listenToMusicVariant"
private const val KEY_SCROLL_SPEED = "scrollSpeed"
private const val KEY_YOUTUBE_MUSIC_DONT_ASK = "youtubeMusicDontAsk"
private const val KEY_VK_MUSIC_DONT_ASK = "vkMusicDontAsk"
private const val KEY_YANDEX_MUSIC_DONT_ASK = "yandexMusicDontAsk"
private const val KEY_VOICE_HELP_DONT_ASK = "voiceHelpDontAsk"

@Singleton
@BoundTo(supertype = Settings::class, component = SingletonComponent::class)
@SuppressLint("ApplySharedPref")
internal class SettingsImpl @Inject constructor(
    @ApplicationContext private val context: Context
): Settings {
    private val sp = context.getSharedPreferences(PREFS_NAME, 0)

    override val appWasUpdated: Boolean
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

    override fun confirmAppUpdate() {
        try {
            val pInfo =
                context.packageManager.getPackageInfo(context.packageName, 0)
            val newVersion = pInfo.versionCode

            val editor = sp.edit()
            editor.putInt(KEY_APK_VERSION, newVersion)
            editor.commit()
        } catch (e: Throwable) {}
    }

    override var theme: Theme
        get() = Theme.values()[sp.getInt(
            KEY_THEME, 0)]
        set(value) {
            val editor = sp.edit()
            editor.putInt(KEY_THEME, value.ordinal)
            editor.commit()
        }

    override var orientation: Orientation
        get() = Orientation.values()[sp.getInt(KEY_ORIENTATION, 0)]
        set(value) {
            val editor = sp.edit()
            editor.putInt(KEY_ORIENTATION, value.ordinal)
            editor.commit()
        }


    override var listenToMusicVariant: ListenToMusicVariant
        get() = ListenToMusicVariant
            .values()[sp.getInt(KEY_LISTEN_TO_MUSIC_VARIANT, 1)]
        set(value) {
            val editor = sp.edit()
            editor.putInt(KEY_LISTEN_TO_MUSIC_VARIANT, value.ordinal)
            editor.commit()
        }

    override var defaultArtist: String
        get() = sp.getString(KEY_DEFAULT_ARTIST, null) ?: ARTIST_KINO
        set(value) {
            val editor = sp.edit()
            editor.putString(KEY_DEFAULT_ARTIST, value)
            editor.commit()
        }

    override var scrollSpeed: Float
        get() = sp.getFloat(KEY_SCROLL_SPEED, 1.0f)
        set(value) {
            val editor = sp.edit()
            editor.putFloat(KEY_SCROLL_SPEED, value)
            editor.commit()
        }

    override var youtubeMusicDontAsk: Boolean
        get() = sp.getBoolean(KEY_YOUTUBE_MUSIC_DONT_ASK, false)
        set(value) {
            val editor = sp.edit()
            editor.putBoolean(KEY_YOUTUBE_MUSIC_DONT_ASK, value)
            editor.commit()
        }

    override var vkMusicDontAsk: Boolean
        get() = sp.getBoolean(KEY_VK_MUSIC_DONT_ASK, false)
        set(value) {
            val editor = sp.edit()
            editor.putBoolean(KEY_VK_MUSIC_DONT_ASK, value)
            editor.commit()
        }

    override var yandexMusicDontAsk: Boolean
        get() = sp.getBoolean(KEY_YANDEX_MUSIC_DONT_ASK, false)
        set(value) {
            val editor = sp.edit()
            editor.putBoolean(KEY_YANDEX_MUSIC_DONT_ASK, value)
            editor.commit()
        }

    override var voiceHelpDontAsk: Boolean
        get() = sp.getBoolean(KEY_VOICE_HELP_DONT_ASK, false)
        set(value) {
            val editor = sp.edit()
            editor.putBoolean(KEY_VOICE_HELP_DONT_ASK, value)
            editor.commit()
        }


    override var commonFontScale: Float
        get() = sp.getFloat(KEY_FONT_SCALE, 1.0f)
        set(value) {
            val editor = sp.edit()
            editor.putFloat(KEY_FONT_SCALE, value)
            editor.commit()
        }

    override val commonFontScaleEnum: FontScale
        get() = FontScale.values().find { it.scale == commonFontScale } ?: FontScale.M

    override fun getSpecificFontScale(scalePow: ScalePow) = commonFontScale.pow(scalePow.pow)
}

