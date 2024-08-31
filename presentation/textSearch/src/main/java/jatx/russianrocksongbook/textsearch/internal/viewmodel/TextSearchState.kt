package jatx.russianrocksongbook.textsearch.internal.viewmodel

import jatx.russianrocksongbook.domain.models.local.Song
import jatx.russianrocksongbook.domain.repository.local.TextSearchOrderBy

data class TextSearchState(
    val currentSongCount: Int,
    val songPosition: Int,
    val currentSong: Song?,
    val songs: List<Song>,
    val searchFor: String,
    val orderBy: TextSearchOrderBy,
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
            orderBy = TextSearchOrderBy.BY_TITLE,
            scrollPosition = 0,
            needScroll = false
        )
    }
}