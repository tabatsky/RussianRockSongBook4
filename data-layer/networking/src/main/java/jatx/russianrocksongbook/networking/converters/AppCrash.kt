package jatx.russianrocksongbook.networking.converters

import jatx.russianrocksongbook.domain.models.appcrash.AppCrash
import jatx.russianrocksongbook.networking.gson.AppCrashGson

internal fun AppCrash.toAppCrashJson() = AppCrashGson(
    appVersionName = appVersionName,
    appVersionCode = appVersionCode,
    androidVersion = androidVersion,
    manufacturer = manufacturer,
    product = product,
    stackTrace = stackTrace
)