package jatx.russianrocksongbook.domain.models.appcrash

import android.os.Build
import jatx.russianrocksongbook.util.debug.exceptionToString

data class AppCrash(
    val appVersionName: String,
    val appVersionCode: Int,
    val androidVersion: String = Build.VERSION.RELEASE,
    val manufacturer: String = Build.MANUFACTURER,
    val product: String = Build.PRODUCT,
    val stackTrace: String
) {
    constructor(version: Version, e: Throwable): this(
        appVersionCode = version.appVersionCode,
        appVersionName = version.appVersionName,
        stackTrace = exceptionToString(e)
    )
}