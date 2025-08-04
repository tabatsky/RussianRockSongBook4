package jatx.russianrocksongbook.textsearch.internal.view.textsearchlist

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import jatx.russianrocksongbook.commonsongtext.viewmodel.UpdateSongListNeedScroll
import jatx.russianrocksongbook.commonview.appbar.CommonSideAppBar
import jatx.russianrocksongbook.commonview.appbar.CommonTopAppBar
import jatx.russianrocksongbook.commonview.theme.LocalAppTheme
import jatx.russianrocksongbook.commonviewmodel.UIAction
import jatx.russianrocksongbook.domain.models.local.Song
import jatx.russianrocksongbook.domain.repository.local.TextSearchOrderBy
import jatx.russianrocksongbook.testing.APP_BAR_TITLE
import jatx.russianrocksongbook.textsearch.R
import jatx.russianrocksongbook.textsearch.internal.viewmodel.PerformTextSearch
import jatx.spinner.SpinnerState

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun TextSearchListScreenImplContent(
    randomKey: Int,
    isBackFromSong: Boolean,
    needScroll: Boolean,
    scrollPosition: Int,
    songs: List<Song>,
    searchFor: String,
    orderBy: TextSearchOrderBy,
    spinnerStateOrderBy: MutableState<SpinnerState>,
    submitAction: (UIAction) -> Unit
) {
    val theme = LocalAppTheme.current

    var savedRandomKey by rememberSaveable { mutableIntStateOf(0) }
    val randomKeyChanged = randomKey != savedRandomKey

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val configuration = LocalConfiguration.current
        val W = configuration.screenWidthDp.dp
        val H = configuration.screenHeightDp.dp

        val isPortrait = W < H
        var isLastOrientationPortrait by rememberSaveable { mutableStateOf(isPortrait) }

        val wasOrientationChanged = isPortrait != isLastOrientationPortrait

        LaunchedEffect(isPortrait) {
            if (wasOrientationChanged) {
                isLastOrientationPortrait = isPortrait
                submitAction(UpdateSongListNeedScroll(true))
            }
        }

        if (randomKeyChanged) {
            savedRandomKey = randomKey
            LaunchedEffect(Unit) {
                if (!isBackFromSong && !wasOrientationChanged) {
                    submitAction(PerformTextSearch("", TextSearchOrderBy.BY_TITLE))
                }
            }
        }

        LaunchedEffect(Unit) {
            if (isBackFromSong) {
                submitAction(UpdateSongListNeedScroll(true))
            }
        }

        if (wasOrientationChanged) return@Box

        @Composable
        fun TheBody(modifier: Modifier) {
            TextSearchListBody(
                modifier = modifier,
                isPortrait = true,
                needScroll = needScroll,
                scrollPosition = scrollPosition,
                songs = songs,
                searchFor = searchFor,
                orderBy = orderBy,
                spinnerStateOrderBy = spinnerStateOrderBy,
                submitAction = submitAction
            )
        }

        if (W < H) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(theme.colorBg)
            ) {
                CommonTopAppBar(
                    title = stringResource(id = R.string.title_activity_text_search_list),
                    titleTestTag = APP_BAR_TITLE
                )
                TheBody(modifier = Modifier.weight(1.0f))
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(theme.colorBg)
            ) {
                CommonSideAppBar(
                    title = stringResource(id = R.string.title_activity_text_search_list),
                    titleTestTag = APP_BAR_TITLE
                )
                TheBody(modifier = Modifier.weight(1.0f))
            }
        }
    }
}
