package jatx.russianrocksongbook.textsearch.internal.view.textsearchlist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import jatx.russianrocksongbook.textsearch.internal.viewmodel.TextSearchViewModel

@Composable
internal fun TextSearchListScreenImpl(randomKey: Int, isBackFromSong: Boolean) {
    val textSearchViewModel = TextSearchViewModel.getInstance()

    val textSearchState by textSearchViewModel.textSearchStateFlow.collectAsState()
    val commonSongTextState by textSearchViewModel.commonSongTextStateFlow.collectAsState()

    val needScroll = commonSongTextState.songListNeedScroll
    val scrollPosition = commonSongTextState.songListScrollPosition

    val songs = textSearchState.songs

    val searchFor = textSearchState.searchFor
    val orderBy = textSearchState.orderBy

    val spinnerStateOrderBy = textSearchViewModel.spinnerStateOrderBy

    val submitAction = textSearchViewModel::submitAction

    TextSearchListScreenImplContent(
        randomKey = randomKey,
        isBackFromSong = isBackFromSong,
        needScroll = needScroll,
        scrollPosition = scrollPosition,
        songs = songs,
        searchFor = searchFor,
        orderBy = orderBy,
        spinnerStateOrderBy = spinnerStateOrderBy,
        submitAction = submitAction
    )
}
