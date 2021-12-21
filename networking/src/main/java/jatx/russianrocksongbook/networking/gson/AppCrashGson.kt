package jatx.russianrocksongbook.networking.gson

import android.os.Build
import jatx.russianrocksongbook.preferences.Version

data class AppCrashGson(
    val appVersionName: String = Version.appVersionName,
    val appVersionCode: Int = Version.appVersionCode,
    val androidVersion: String = Build.VERSION.RELEASE,
    val manufacturer: String = Build.MANUFACTURER,
    val product: String = Build.PRODUCT,
    val stackTrace: String
)