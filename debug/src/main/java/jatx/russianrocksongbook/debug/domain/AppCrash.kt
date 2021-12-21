package jatx.russianrocksongbook.debug.domain

import android.os.Build
import jatx.russianrocksongbook.debug.debug.exceptionToString
import jatx.russianrocksongbook.preferences.Version

data class AppCrash(
    val appVersionName: String = Version.appVersionName,
    val appVersionCode: Int = Version.appVersionCode,
    val androidVersion: String = Build.VERSION.RELEASE,
    val manufacturer: String = Build.MANUFACTURER,
    val product: String = Build.PRODUCT,
    val stackTrace: String
) {
    constructor(e: Throwable): this(stackTrace = exceptionToString(e))
}