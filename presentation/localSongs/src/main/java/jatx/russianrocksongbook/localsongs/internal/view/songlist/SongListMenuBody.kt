package jatx.russianrocksongbook.localsongs.internal.view.songlist

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.itemsIndexed
import androidx.tv.foundation.lazy.list.rememberTvLazyListState
import jatx.russianrocksongbook.commonview.font.toScaledSp
import jatx.russianrocksongbook.commonview.theme.LocalAppTheme
import jatx.russianrocksongbook.domain.repository.local.predefinedArtistList
import jatx.russianrocksongbook.domain.repository.local.predefinedArtistsWithGroups
import jatx.russianrocksongbook.domain.repository.preferences.ScalePow
import jatx.russianrocksongbook.localsongs.R
import jatx.russianrocksongbook.localsongs.internal.viewmodel.LocalViewModel
import jatx.russianrocksongbook.localsongs.internal.viewmodel.SelectArtist
import jatx.russianrocksongbook.localsongs.internal.viewmodel.UpdateMenuExpandedArtistGroup
import jatx.russianrocksongbook.localsongs.internal.viewmodel.UpdateMenuScrollPosition
import jatx.russianrocksongbook.testing.MENU_LAZY_COLUMN
import jatx.russianrocksongbook.testing.TestingConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@Composable
internal fun SongListMenuBody(
    navigationFocusRequester: FocusRequester,
    onCloseDrawer: () -> Unit
) {
    val localViewModel = LocalViewModel.getInstance()

    val theme = LocalAppTheme.current

    val localState by localViewModel.localStateFlow.collectAsState()
    val appState by localViewModel.appStateFlow.collectAsState()
    val artistList = appState.artistList

    val predefinedWithGroups = artistList.predefinedArtistsWithGroups()

    val fontSizeSp = dimensionResource(id = R.dimen.text_size_20)
        .toScaledSp(ScalePow.MENU)

    val modifier = Modifier
        .testTag(MENU_LAZY_COLUMN)
        .focusProperties {
            left = navigationFocusRequester
        }

    val expandedArtistGroup = localState.menuExpandedArtistGroup
    fun getExpandedList(group: String) = if (expandedArtistGroup == group) {
        artistList.filter { it !in predefinedArtistList && it.uppercase().startsWith(group) }
    } else {
        listOf()
    }


    @Composable
    fun TheItem(artistOrGroup: String) {
        if (artistOrGroup in predefinedArtistList) {
            ArtistItem(
                artist = artistOrGroup,
                fontSizeSp = fontSizeSp,
                theme = theme,
                onClick = {
                    onCloseDrawer()
                    localViewModel.submitAction(SelectArtist(artistOrGroup))
                }
            )
        } else {
            ArtistGroupItem(
                artistGroup = artistOrGroup,
                expandedList = getExpandedList(artistOrGroup),
                fontSizeSp = fontSizeSp,
                theme = theme,
                onGroupClick = {
                    localViewModel.submitAction(UpdateMenuExpandedArtistGroup(artistOrGroup))
                },
                onArtistClick = {
                    onCloseDrawer()
                    localViewModel.submitAction(SelectArtist(it))
                }
            )
        }
    }

    var needScroll by remember {
        mutableStateOf(true)
    }

    val scrollPosition = localState.menuScrollPosition

    @Composable
    fun ScrollEffect(
        onPerformScroll: suspend (Int) -> Unit,
        getFirstVisibleItemIndex: () -> Int
    ) {
        LaunchedEffect(needScroll) {
            if (needScroll) {
                if (TestingConfig.isTesting) {
                    delay(100L)
                }
                if (scrollPosition >= 0) onPerformScroll(scrollPosition)
                needScroll = false
            } else {
                snapshotFlow {
                    getFirstVisibleItemIndex()
                }.collectLatest {
                    localViewModel.submitAction(UpdateMenuScrollPosition(it))
                }
            }
        }
    }

    if (localViewModel.isTV) {
        val menuState = rememberTvLazyListState()
        TvLazyColumn(
            modifier = modifier,
            state = menuState
        ) {
            itemsIndexed(predefinedWithGroups) { _, artistOrGroup ->
                TheItem(artistOrGroup = artistOrGroup)
            }
        }
        ScrollEffect(
            onPerformScroll = { menuState.scrollToItem(it) },
            getFirstVisibleItemIndex = { menuState.firstVisibleItemIndex }
        )
    } else {
        val menuState = rememberLazyListState()
        LazyColumn(
            modifier = modifier,
            state = menuState
        ) {
            itemsIndexed(predefinedWithGroups) { _, artistOrGroup ->
                TheItem(artistOrGroup = artistOrGroup)
            }
        }
        ScrollEffect(
            onPerformScroll = { menuState.scrollToItem(it) },
            getFirstVisibleItemIndex = { menuState.firstVisibleItemIndex }
        )
    }
}
