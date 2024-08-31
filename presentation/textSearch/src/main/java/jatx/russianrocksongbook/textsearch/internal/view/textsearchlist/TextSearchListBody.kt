package jatx.russianrocksongbook.textsearch.internal.view.textsearchlist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import jatx.russianrocksongbook.commonview.font.toScaledSp
import jatx.russianrocksongbook.commonview.stub.CommonSongListStub
import jatx.russianrocksongbook.commonview.theme.LocalAppTheme
import jatx.russianrocksongbook.domain.models.local.Song
import jatx.russianrocksongbook.domain.repository.cloud.OrderBy
import jatx.russianrocksongbook.domain.repository.preferences.ScalePow
import jatx.russianrocksongbook.testing.TestingConfig
import jatx.russianrocksongbook.textsearch.R
import jatx.russianrocksongbook.textsearch.internal.viewmodel.PerformTextSearch
import jatx.russianrocksongbook.textsearch.internal.viewmodel.TextSearchViewModel
import jatx.russianrocksongbook.textsearch.internal.viewmodel.UpdateOrderBy
import jatx.russianrocksongbook.textsearch.internal.viewmodel.UpdateSearchFor
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@Composable
internal fun TextSearchListBody(
    modifier: Modifier,
    isPortrait: Boolean
) {
    val textSearchViewModel = TextSearchViewModel.getInstance()
    val theme = LocalAppTheme.current

    val textSearchState by textSearchViewModel.textSearchStateFlow.collectAsState()

    val needScroll = textSearchState.needScroll
    val scrollPosition = textSearchState.scrollPosition

    val songs = textSearchState.songs

    val fontSizeTextSp = dimensionResource(id = R.dimen.text_size_16)
        .toScaledSp(ScalePow.TEXT)
    val fontSizeSongTitleSp = dimensionResource(id = R.dimen.text_size_20)
        .toScaledSp(ScalePow.TEXT)
    val fontSizeArtistSp = dimensionResource(id = R.dimen.text_size_24)
        .toScaledSp(ScalePow.TEXT)

    val searchFor = textSearchState.searchFor
    val onSearchForValueChange: (String) -> Unit = {
        textSearchViewModel.submitAction(UpdateSearchFor(it))
    }

    val orderBy = textSearchState.orderBy
    val onOrderByValueChange: (OrderBy) -> Unit = {
        if (orderBy != it) {
            textSearchViewModel.submitAction(UpdateOrderBy(it))
            textSearchViewModel.submitAction(PerformTextSearch(searchFor, it))
        }
    }

    val onSearchClick = {
        textSearchViewModel.submitAction(PerformTextSearch(searchFor, orderBy))
    }

    val onItemClick: (Int, Song) -> Unit = { index, song ->
        println("selected: ${song.artist} - ${song.title}")
//        textSearchViewModel.submitAction(SelectScreen(ScreenVariant.CloudSongText(index)))
    }

    Column(
        modifier = modifier
    ) {
        if (isPortrait) {
            TextSearchPanelPortrait(
                searchFor = searchFor,
                orderBy = orderBy,
                theme = theme,
                fontSizeTextSp = fontSizeTextSp,
                onSearchForValueChange = onSearchForValueChange,
                onOrderByValueChange = onOrderByValueChange,
                onSearchClick = onSearchClick
            )
        } else {
            TextSearchPanelLandscape(
                searchFor = searchFor,
                orderBy = orderBy,
                theme = theme,
                fontSizeTextSp = fontSizeTextSp,
                onSearchForValueChange = onSearchForValueChange,
                onOrderByValueChange = onOrderByValueChange,
                onSearchClick = onSearchClick
            )
        }

        val listState = rememberLazyListState()

        if (songs.isEmpty()) {
            CommonSongListStub(fontSizeSongTitleSp, theme)
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                state = listState
            ) {
                items(songs.size) { index ->
                    val song = songs[index]
                    song.let {
                        SongItem(
                            it, theme, fontSizeArtistSp, fontSizeSongTitleSp
                        ) {
                            onItemClick(index, it)
                        }
                    }
                }
            }

            LaunchedEffect(needScroll) {
                if (needScroll) {
                    if (scrollPosition < songs.size) {
                        if (TestingConfig.isTesting) {
                            delay(100L)
                        }
                        listState.scrollToItem(scrollPosition)
                        delay(100L)
//                        textSearchViewModel.submitAction(UpdateCloudSongListNeedScroll(false))
                    }
                } else {
                    snapshotFlow {
                        listState.firstVisibleItemIndex
                    }.collectLatest {
//                        textSearchViewModel.submitAction(UpdateCloudSongListScrollPosition(it))
                    }
                }
            }
        }
    }
}
