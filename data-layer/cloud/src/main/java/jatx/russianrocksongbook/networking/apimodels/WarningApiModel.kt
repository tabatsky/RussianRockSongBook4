package jatx.russianrocksongbook.networking.apimodels

internal data class WarningApiModel(
    var warningType: String,
    var artist: String,
    var title: String,
    var variant: Int,
    var comment: String
)