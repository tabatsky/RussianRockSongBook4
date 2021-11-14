package jatx.russianrocksongbook.domain

import android.os.Build
import jatx.russianrocksongbook.data.gson.AppCrashGson
import jatx.russianrocksongbook.debug.exceptionToString
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

    fun toAppCrashJson() = AppCrashGson(
        appVersionName, appVersionCode, androidVersion, manufacturer, product, stackTrace
    )
}