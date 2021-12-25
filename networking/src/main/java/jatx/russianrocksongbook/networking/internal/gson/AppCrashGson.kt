package jatx.russianrocksongbook.networking.internal.gson

internal data class AppCrashGson(
    val appVersionName: String,
    val appVersionCode: Int,
    val androidVersion: String,
    val manufacturer: String,
    val product: String,
    val stackTrace: String
)