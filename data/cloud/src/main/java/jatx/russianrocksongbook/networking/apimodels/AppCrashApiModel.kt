package jatx.russianrocksongbook.networking.apimodels

internal data class AppCrashApiModel(
    val appVersionName: String,
    val appVersionCode: Int,
    val androidVersion: String,
    val manufacturer: String,
    val product: String,
    val stackTrace: String
)