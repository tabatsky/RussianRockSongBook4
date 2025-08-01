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
import jatx.russianrocksongbook.commonview.font.toScaledSp
import jatx.russianrocksongbook.commonview.theme.LocalAppTheme
import jatx.russianrocksongbook.commonviewmodel.UIAction
import jatx.russianrocksongbook.domain.repository.local.predefinedArtistList
import jatx.russianrocksongbook.domain.repository.local.predefinedArtistsWithGroups
import jatx.russianrocksongbook.domain.repository.preferences.ScalePow
import jatx.russianrocksongbook.localsongs.R
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
    onCloseDrawer: () -> Unit,
    artistList: List<String>,
    menuExpandedArtistGroup: String,
    menuScrollPosition: Int,
    submitAction: (UIAction) -> Unit
) {
    val theme = LocalAppTheme.current

    val predefinedWithGroups = artistList.predefinedArtistsWithGroups()

    val fontSizeSp = dimensionResource(id = R.dimen.text_size_20)
        .toScaledSp(ScalePow.MENU)

    val modifier = Modifier
        .testTag(MENU_LAZY_COLUMN)
        .focusProperties {
            left = navigationFocusRequester
        }

    val expandedArtistGroup = menuExpandedArtistGroup
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
                    submitAction(SelectArtist(artistOrGroup))
                }
            )
        } else {
            ArtistGroupItem(
                artistGroup = artistOrGroup,
                expandedList = getExpandedList(artistOrGroup),
                fontSizeSp = fontSizeSp,
                theme = theme,
                onGroupClick = {
                    submitAction(UpdateMenuExpandedArtistGroup(artistOrGroup))
                },
                onArtistClick = {
                    onCloseDrawer()
                    submitAction(SelectArtist(it))
                }
            )
        }
    }

    var needScroll by remember {
        mutableStateOf(true)
    }

    val scrollPosition = menuScrollPosition

    @Composable
    fun ScrollEffect(
        onPerformScroll: suspend (Int) -> Unit,
        getFirstVisibleItemIndex: () -> Int
    ) {
        LaunchedEffect(needScroll) {
            if (needScroll) {
                if (TestingConfig.isUITesting) {
                    delay(100L)
                }
                if (scrollPosition >= 0) onPerformScroll(scrollPosition)
                needScroll = false
            } else {
                snapshotFlow {
                    getFirstVisibleItemIndex()
                }.collectLatest {
                    submitAction(UpdateMenuScrollPosition(it))
                }
            }
        }
    }

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
