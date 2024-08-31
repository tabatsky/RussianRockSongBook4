package jatx.russianrocksongbook.textsearch.internal.viewmodel

import jatx.russianrocksongbook.domain.models.local.Song
import jatx.russianrocksongbook.domain.repository.local.TextSearchOrderBy

data class TextSearchState(
    val currentSongCount: Int,
    val currentSongPosition: Int,
    val currentSong: Song?,
    val songs: List<Song>,
    val searchFor: String,
    val orderBy: TextSearchOrderBy,
    val scrollPosition: Int,
    val needScroll: Boolean,
    val isEditorMode: Boolean,
    val isAutoPlayMode: Boolean,
    val isUploadButtonEnabled: Boolean,
) {
    companion object {
        fun initial() = TextSearchState(
            currentSongCount = 0,
            currentSongPosition = 0,
            currentSong = null,
            songs = listOf(),
            searchFor = "",
            orderBy = TextSearchOrderBy.BY_TITLE,
            scrollPosition = 0,
            needScroll = false,
            isEditorMode = false,
            isAutoPlayMode = false,
            isUploadButtonEnabled = true
        )
    }
}