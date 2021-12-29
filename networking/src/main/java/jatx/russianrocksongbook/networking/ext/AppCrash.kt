package jatx.russianrocksongbook.networking.ext

import jatx.russianrocksongbook.domain.models.AppCrash
import jatx.russianrocksongbook.networking.gson.AppCrashGson

internal fun AppCrash.toAppCrashJson() = AppCrashGson(
    appVersionName, appVersionCode, androidVersion, manufacturer, product, stackTrace
)