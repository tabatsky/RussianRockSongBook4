package jatx.russianrocksongbook.preferences

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import dagger.hilt.android.qualifiers.ApplicationContext
import jatx.russianrocksongbook.domain.util.HashingUtil
import javax.inject.Inject
import javax.inject.Singleton

const val USER_PREFS_NAME = "RussianRockUserPrefs"

const val KEY_GOOGLE_ACCOUNT = "googleAccount"

@Singleton
class UserInfo @Inject constructor(
    @ApplicationContext private val context: Context) {

    private val sp = context.getSharedPreferences(USER_PREFS_NAME, 0)

    val deviceIdHash: String
        @SuppressLint("HardwareIds")
        get() {
            val deviceId = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            )
            return HashingUtil.md5(deviceId)
        }

    var googleAccount: String
        get() = sp.getString(KEY_GOOGLE_ACCOUNT, null) ?: ""
        @SuppressLint("ApplySharedPref")
        set(value) {
            val editor = sp.edit()
            editor.putString(KEY_GOOGLE_ACCOUNT, value)
            editor.commit()
        }
}