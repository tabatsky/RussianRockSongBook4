package jatx.russianrocksongbook.networking.converters

import jatx.russianrocksongbook.domain.models.appcrash.AppCrash
import jatx.russianrocksongbook.networking.apimodels.AppCrashApiModel

internal fun AppCrash.toAppCrashApiModel() = AppCrashApiModel(
    appVersionName = appVersionName,
    appVersionCode = appVersionCode,
    androidVersion = androidVersion,
    manufacturer = manufacturer,
    product = product,
    stackTrace = stackTrace
)