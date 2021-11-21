package jatx.russianrocksongbook.model.api.gson

import android.os.Build
import jatx.russianrocksongbook.model.version.Version

data class AppCrashGson(
    val appVersionName: String = Version.appVersionName,
    val appVersionCode: Int = Version.appVersionCode,
    val androidVersion: String = Build.VERSION.RELEASE,
    val manufacturer: String = Build.MANUFACTURER,
    val product: String = Build.PRODUCT,
    val stackTrace: String
)