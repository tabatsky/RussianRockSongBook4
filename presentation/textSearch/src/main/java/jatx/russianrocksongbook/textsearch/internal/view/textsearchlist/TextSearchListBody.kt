package jatx.russianrocksongbook.textsearch.internal.view.textsearchlist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import jatx.russianrocksongbook.commonsongtext.viewmodel.UpdateSongListNeedScroll
import jatx.russianrocksongbook.commonsongtext.viewmodel.UpdateSongListScrollPosition
import jatx.russianrocksongbook.commonview.font.toScaledSp
import jatx.russianrocksongbook.commonview.stub.CommonSongListStub
import jatx.russianrocksongbook.commonview.theme.LocalAppTheme
import jatx.russianrocksongbook.commonviewmodel.SelectScreen
import jatx.russianrocksongbook.commonviewmodel.UIAction
import jatx.russianrocksongbook.domain.models.local.Song
import jatx.russianrocksongbook.domain.repository.local.TextSearchOrderBy
import jatx.russianrocksongbook.domain.repository.preferences.ScalePow
import jatx.russianrocksongbook.navigation.ScreenVariant
import jatx.russianrocksongbook.testing.TestingConfig
import jatx.russianrocksongbook.textsearch.R
import jatx.russianrocksongbook.textsearch.internal.viewmodel.PerformTextSearch
import jatx.russianrocksongbook.textsearch.internal.viewmodel.UpdateOrderBy
import jatx.russianrocksongbook.textsearch.internal.viewmodel.UpdateSearchFor
import jatx.spinner.SpinnerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@Composable
internal fun TextSearchListBody(
    modifier: Modifier,
    isPortrait: Boolean,
    needScroll: Boolean,
    scrollPosition: Int,
    songs: List<Song>,
    searchFor: String,
    orderBy: TextSearchOrderBy,
    spinnerStateOrderBy: MutableState<SpinnerState>,
    submitAction: (UIAction) -> Unit
) {
    val theme = LocalAppTheme.current

    val fontSizeTextSp = dimensionResource(id = R.dimen.text_size_16)
        .toScaledSp(ScalePow.TEXT)
    val fontSizeSongTitleSp = dimensionResource(id = R.dimen.text_size_20)
        .toScaledSp(ScalePow.TEXT)
    val fontSizeArtistSp = dimensionResource(id = R.dimen.text_size_24)
        .toScaledSp(ScalePow.TEXT)

    val onSearchForValueChange: (String) -> Unit = {
        submitAction(UpdateSearchFor(it))
    }

    val onOrderByValueChange: (TextSearchOrderBy) -> Unit = {
        if (orderBy != it) {
            submitAction(UpdateOrderBy(it))
            submitAction(PerformTextSearch(searchFor, it))
        }
    }

    val onSearchClick = {
        submitAction(PerformTextSearch(searchFor, orderBy))
    }

    val onItemClick: (Int, Song) -> Unit = { index, song ->
        println("selected: ${song.artist} - ${song.title}")
        submitAction(SelectScreen(ScreenVariant.TextSearchSongText(index)))
    }

    Column(
        modifier = modifier
    ) {
        if (isPortrait) {
            TextSearchListPanelPortrait(
                searchFor = searchFor,
                orderBy = orderBy,
                theme = theme,
                fontSizeTextSp = fontSizeTextSp,
                onSearchForValueChange = onSearchForValueChange,
                onOrderByValueChange = onOrderByValueChange,
                onSearchClick = onSearchClick,
                spinnerStateOrderBy = spinnerStateOrderBy
            )
        } else {
            TextSearchListPanelLandscape(
                searchFor = searchFor,
                orderBy = orderBy,
                theme = theme,
                fontSizeTextSp = fontSizeTextSp,
                onSearchForValueChange = onSearchForValueChange,
                onOrderByValueChange = onOrderByValueChange,
                onSearchClick = onSearchClick,
                spinnerStateOrderBy = spinnerStateOrderBy
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
                        if (TestingConfig.isUITesting) {
                            delay(100L)
                        }
                        listState.scrollToItem(scrollPosition)
                        delay(100L)
                        submitAction(UpdateSongListNeedScroll(false))
                    }
                } else {
                    snapshotFlow {
                        listState.firstVisibleItemIndex
                    }.collectLatest {
                        submitAction(UpdateSongListScrollPosition(it))
                    }
                }
            }
        }
    }
}
