package jatx.russianrocksongbook.networking.internal.ext

import jatx.russianrocksongbook.debug.domain.AppCrash
import jatx.russianrocksongbook.networking.internal.gson.AppCrashGson

internal fun AppCrash.toAppCrashJson() = AppCrashGson(
    appVersionName, appVersionCode, androidVersion, manufacturer, product, stackTrace
)