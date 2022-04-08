package jatx.russianrocksongbook.domain.models.warning

import jatx.russianrocksongbook.domain.models.cloud.CloudSong
import jatx.russianrocksongbook.domain.models.local.Song

const val TYPE_CLOUD = "cloud"
const val TYPE_OUT_OF_THE_BOX = "outOfTheBox"

@Suppress("DataClassPrivateConstructor")
data class Warning private constructor(
    var warningType: String,
    var artist: String,
    var title: String,
    var variant: Int,
    var comment: String
) {

    constructor(song: Song, comment: String): this(
        warningType = TYPE_OUT_OF_THE_BOX,
        artist = song.artist,
        title = song.title,
        variant = -1,
        comment = comment
    )

    constructor(cloudSong: CloudSong, comment: String): this(
        warningType = TYPE_CLOUD,
        artist = cloudSong.artist,
        title = cloudSong.title,
        variant = cloudSong.variant,
        comment = comment
    )
}