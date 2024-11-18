package jatx.russianrocksongbook.textsearch.internal.view.textsearchlist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import jatx.russianrocksongbook.domain.models.local.Song
import jatx.russianrocksongbook.domain.repository.local.TextSearchOrderBy
import jatx.spinner.SpinnerState

@Preview
@Composable
fun TextSearchListScreenImplPreviewPortrait() {
    val needScroll = false
    val scrollPosition = 0

    val songs = (1..30)
        .map {
            Song(
                artist = "Исполнитель $it",
                title = "Название $it"
            )
        }

    val searchFor = ""
    val orderBy = TextSearchOrderBy.BY_TITLE

    val spinnerStateOrderBy = remember {
        mutableStateOf(SpinnerState(0, false))
    }

    TextSearchListScreenImplContent(
        randomKey = 1237,
        isBackFromSong = false,
        needScroll = needScroll,
        scrollPosition = scrollPosition,
        songs = songs,
        searchFor = searchFor,
        orderBy = orderBy,
        spinnerStateOrderBy = spinnerStateOrderBy,
        submitAction = {}
    )
}