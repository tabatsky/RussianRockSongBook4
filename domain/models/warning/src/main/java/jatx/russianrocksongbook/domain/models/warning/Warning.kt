package jatx.russianrocksongbook.domain.models.warning

const val TYPE_CLOUD = "cloud"
const val TYPE_OUT_OF_THE_BOX = "outOfTheBox"

data class Warning(
    val warningType: String,
    val artist: String,
    val title: String,
    val variant: Int,
    val comment: String
)