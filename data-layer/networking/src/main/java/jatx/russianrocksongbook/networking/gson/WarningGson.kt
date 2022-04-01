package jatx.russianrocksongbook.networking.gson

import jatx.russianrocksongbook.domain.models.cloud.CloudSong
import jatx.russianrocksongbook.domain.models.local.Song

const val TYPE_CLOUD = "cloud"
const val TYPE_OUT_OF_THE_BOX = "outOfTheBox"

data class WarningGson(
    var warningType: String = TYPE_OUT_OF_THE_BOX,
    var artist: String = "",
    var title: String = "",
    var variant: Int = 0,
    var comment: String = ""
) {

    constructor(song: Song, comment: String): this() {
        warningType = TYPE_OUT_OF_THE_BOX
        artist = song.artist
        title = song.title
        variant = -1
        this.comment = comment
    }

    constructor(cloudSong: CloudSong, comment: String): this() {
        warningType = TYPE_CLOUD
        artist = cloudSong.artist
        title = cloudSong.title
        variant = cloudSong.variant
        this.comment = comment
    }
}