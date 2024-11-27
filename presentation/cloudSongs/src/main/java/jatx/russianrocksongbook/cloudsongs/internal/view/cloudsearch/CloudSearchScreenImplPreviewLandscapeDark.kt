package jatx.russianrocksongbook.cloudsongs.internal.view.cloudsearch

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import jatx.russianrocksongbook.cloudsongs.internal.viewmodel.SearchState
import jatx.russianrocksongbook.commonview.theme.DarkTheme
import jatx.russianrocksongbook.domain.models.cloud.CloudSong
import jatx.russianrocksongbook.domain.repository.cloud.CloudSearchOrderBy
import jatx.spinner.SpinnerState
import kotlinx.coroutines.flow.flowOf

@Preview(widthDp = 640, heightDp = 360)
@Composable
internal fun CloudSearchScreenImplPreviewLandscapeDark() {
    val searchState = SearchState.PAGE_LOADING_SUCCESS

    val needScroll = false
    val scrollPosition = 0

    val cloudSongs = (1..30)
        .map {
            CloudSong(
                artist = "Исполнитель $it",
                title = "Название $it",
                text = "Текст текст\nТекст\nAm Em\nТекст\n",
                variant = 1,
                likeCount = 2,
                dislikeCount = 1
            )
        }

    val cloudSongItems = flowOf(PagingData.from(cloudSongs)).collectAsLazyPagingItems()

    val searchFor = "abc"
    val orderBy = CloudSearchOrderBy.BY_ID_DESC

    val spinnerStateOrderBy = remember {
        mutableStateOf(SpinnerState(0, false))
    }

    DarkTheme {
        CloudSearchScreenImplContent(
            randomKey = 1237,
            isBackFromSong = false,
            searchState = searchState,
            needScroll = needScroll,
            scrollPosition = scrollPosition,
            cloudSongItems = cloudSongItems,
            searchFor = searchFor,
            orderBy = orderBy,
            spinnerStateOrderBy = spinnerStateOrderBy,
            submitAction = {}
        )
    }
}