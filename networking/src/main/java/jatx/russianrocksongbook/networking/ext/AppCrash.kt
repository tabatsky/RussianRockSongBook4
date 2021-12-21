package jatx.russianrocksongbook.networking.ext

import jatx.russianrocksongbook.debug.domain.AppCrash
import jatx.russianrocksongbook.networking.gson.AppCrashGson

fun AppCrash.toAppCrashJson() = AppCrashGson(
    appVersionName, appVersionCode, androidVersion, manufacturer, product, stackTrace
)