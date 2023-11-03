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

@Singleton
@BoundTo(supertype = UserInfo::class, component = SingletonComponent::class)
class UserInfoImpl @Inject constructor(
    @ApplicationContext private val context: Context)
    : UserInfo {

    override val deviceIdHash by lazy {
        @SuppressLint("HardwareIds")
        val deviceId = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
        HashingUtil.hash(deviceId)
    }

    override val googleAccount: String
        get() = deviceIdHash
}