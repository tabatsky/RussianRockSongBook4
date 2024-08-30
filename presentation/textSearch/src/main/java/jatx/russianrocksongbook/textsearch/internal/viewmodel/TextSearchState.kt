package jatx.russianrocksongbook.textsearch.internal.viewmodel

import jatx.russianrocksongbook.domain.models.local.Song
import jatx.russianrocksongbook.domain.repository.cloud.OrderBy

data class TextSearchState(
    val currentSongCount: Int,
    val songPosition: Int,
    val currentSong: Song?,
    val songs: List<Song>,
    val searchFor: String,
    val orderBy: OrderBy,
    val scrollPosition: Int,
    val needScroll: Boolean
) {
    companion object {
        fun initial() = TextSearchState(
            currentSongCount = 0,
            songPosition = 0,
            currentSong = null,
            songs = listOf(),
            searchFor = "",
            orderBy = OrderBy.BY_ID_DESC,
            scrollPosition = 0,
            needScroll = false
        )
    }
}