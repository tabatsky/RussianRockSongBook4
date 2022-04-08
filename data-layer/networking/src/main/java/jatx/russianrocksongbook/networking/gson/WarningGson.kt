package jatx.russianrocksongbook.networking.gson

internal data class WarningGson(
    var warningType: String,
    var artist: String,
    var title: String,
    var variant: Int,
    var comment: String
)