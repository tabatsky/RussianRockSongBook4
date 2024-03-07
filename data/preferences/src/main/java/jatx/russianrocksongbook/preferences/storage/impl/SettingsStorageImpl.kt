package jatx.russianrocksongbook.preferences.storage.impl

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import it.czerwinski.android.hilt.annotations.BoundTo
import jatx.russianrocksongbook.domain.repository.preferences.ARTIST_KINO
import jatx.russianrocksongbook.preferences.storage.SettingsStorage
import javax.inject.Inject
import javax.inject.Singleton

internal const val PREFS_NAME = "RussianRockPreferences"

internal const val KEY_APK_VERSION = "apkVersion"
internal const val KEY_THEME = "theme_int"
internal const val KEY_ORIENTATION = "orientation_int"
internal const val KEY_DEFAULT_ARTIST = "defaultArtist"
internal const val KEY_FONT_SCALE = "fontScale"
internal const val KEY_LISTEN_TO_MUSIC_VARIANT = "listenToMusicVariant"
internal const val KEY_SCROLL_SPEED = "scrollSpeed"
internal const val KEY_YOUTUBE_MUSIC_DONT_ASK = "youtubeMusicDontAsk"
internal const val KEY_VK_MUSIC_DONT_ASK = "vkMusicDontAsk"
internal const val KEY_YANDEX_MUSIC_DONT_ASK = "yandexMusicDontAsk"
internal const val KEY_VOICE_HELP_DONT_ASK = "voiceHelpDontAsk"

@Singleton
@BoundTo(supertype = SettingsStorage::class, component = SingletonComponent::class)
@SuppressLint("ApplySharedPref")
internal class SettingsStorageImpl @Inject constructor(
    @ApplicationContext private val context: Context
): SettingsStorage {
    private val sp = context.getSharedPreferences(PREFS_NAME, 0)

    @Suppress("DEPRECATION")
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


    @Suppress("DEPRECATION")
    override fun confirmAppUpdate() {
        try {
            val pInfo =
                context.packageManager.getPackageInfo(context.packageName, 0)
            val newVersion = pInfo.versionCode

            val editor = sp.edit()
            editor.putInt(KEY_APK_VERSION, newVersion)
            editor.commit()
        } catch (_: Throwable) {}
    }

    override var theme: Int
        get() = sp.getInt(KEY_THEME, 0)
        set(value) {
            val editor = sp.edit()
            editor.putInt(KEY_THEME, value)
            editor.commit()
        }

    override var orientation: Int
        get() = sp.getInt(KEY_ORIENTATION, 0)
        set(value) {
            val editor = sp.edit()
            editor.putInt(KEY_ORIENTATION, value)
            editor.commit()
        }

    override var listenToMusicVariant: Int
        get() = sp.getInt(KEY_LISTEN_TO_MUSIC_VARIANT, 1)
        set(value) {
            val editor = sp.edit()
            editor.putInt(KEY_LISTEN_TO_MUSIC_VARIANT, value)
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
}