package jatx.russianrocksongbook.data

import android.content.Context
import android.provider.Settings
import jatx.russianrocksongbook.db.util.HashingUtil

const val USER_PREFS_NAME = "RussianRockUserPrefs"

const val KEY_GOOGLE_ACCOUNT = "googleAccount"

class UserInfo(private val context: Context) {

    private val sp = context.getSharedPreferences(USER_PREFS_NAME, 0)

    val deviceIdHash: String
        get() {
            val deviceId = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            )
            return HashingUtil.md5(deviceId)
        }

    var googleAccount: String
        get() = sp.getString(KEY_GOOGLE_ACCOUNT, null) ?: ""
        set(value) {
            val editor = sp.edit()
            editor.putString(KEY_GOOGLE_ACCOUNT, value)
            editor.commit()
        }
}