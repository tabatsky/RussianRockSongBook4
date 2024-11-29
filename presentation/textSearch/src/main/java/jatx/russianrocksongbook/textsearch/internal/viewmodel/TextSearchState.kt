package jatx.russianrocksongbook.textsearch.internal.viewmodel

import jatx.russianrocksongbook.domain.models.local.Song
import jatx.russianrocksongbook.domain.repository.local.TextSearchOrderBy

data class TextSearchState(
    val songs: List<Song>,
    val searchFor: String,
    val orderBy: TextSearchOrderBy
) {
    companion object {
        fun initial() = TextSearchState(
            songs = listOf(),
            searchFor = "",
            orderBy = TextSearchOrderBy.BY_TITLE
        )
    }
}