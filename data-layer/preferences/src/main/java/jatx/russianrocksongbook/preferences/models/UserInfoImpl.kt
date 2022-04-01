package jatx.russianrocksongbook.preferences.models

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import it.czerwinski.android.hilt.annotations.BoundTo
import jatx.russianrocksongbook.domain.models.cloud.UserInfo
import jatx.russianrocksongbook.util.hashing.HashingUtil
import javax.inject.Inject
import javax.inject.Singleton

const val USER_PREFS_NAME = "RussianRockUserPrefs"

const val KEY_GOOGLE_ACCOUNT = "googleAccount"

@Singleton
@BoundTo(supertype = UserInfo::class, component = SingletonComponent::class)
class UserInfoImpl @Inject constructor(
    @ApplicationContext private val context: Context)
    : UserInfo {

    private val sp = context.getSharedPreferences(USER_PREFS_NAME, 0)

    override val deviceIdHash: String
        @SuppressLint("HardwareIds")
        get() {
            val deviceId = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            )
            return HashingUtil.md5(deviceId)
        }

    override var googleAccount: String
        get() = sp.getString(KEY_GOOGLE_ACCOUNT, null) ?: ""
        @SuppressLint("ApplySharedPref")
        set(value) {
            val editor = sp.edit()
            editor.putString(KEY_GOOGLE_ACCOUNT, value)
            editor.commit()
        }
}