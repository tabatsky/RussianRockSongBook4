package jatx.russianrocksongbook.textsearch.internal.view.textsearchlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import jatx.russianrocksongbook.commonview.appbar.CommonSideAppBar
import jatx.russianrocksongbook.commonview.appbar.CommonTopAppBar
import jatx.russianrocksongbook.commonview.theme.LocalAppTheme
import jatx.russianrocksongbook.domain.repository.local.TextSearchOrderBy
import jatx.russianrocksongbook.testing.APP_BAR_TITLE
import jatx.russianrocksongbook.textsearch.R
import jatx.russianrocksongbook.textsearch.internal.viewmodel.PerformTextSearch
import jatx.russianrocksongbook.textsearch.internal.viewmodel.TextSearchViewModel
import jatx.russianrocksongbook.textsearch.internal.viewmodel.UpdateSongListNeedScroll

@Composable
internal fun TextSearchListScreenImpl(randomKey: Int, isBackFromSong: Boolean) {
    val textSearchViewModel = TextSearchViewModel.getInstance()
    val theme = LocalAppTheme.current

    var savedRandomKey by rememberSaveable { mutableIntStateOf(0) }
    val randomKeyChanged = randomKey != savedRandomKey

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val W = this.maxWidth
        val H = this.maxHeight

        val isPortrait = W < H
        var isLastOrientationPortrait by rememberSaveable { mutableStateOf(isPortrait) }

        val wasOrientationChanged = isPortrait != isLastOrientationPortrait

        LaunchedEffect(isPortrait) {
            if (wasOrientationChanged) {
                isLastOrientationPortrait = isPortrait
                textSearchViewModel.submitAction(UpdateSongListNeedScroll(true))
            }
        }

        if (randomKeyChanged) {
            savedRandomKey = randomKey
            LaunchedEffect(Unit) {
                if (!isBackFromSong && !wasOrientationChanged) {
                    textSearchViewModel.submitAction(PerformTextSearch("", TextSearchOrderBy.BY_TITLE))
                }
            }
        }

        LaunchedEffect(Unit) {
            if (isBackFromSong) {
                textSearchViewModel.submitAction(UpdateSongListNeedScroll(true))
            }
        }

        if (wasOrientationChanged) return@BoxWithConstraints

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
                TextSearchListBody(
                    modifier = Modifier.weight(1.0f),
                    isPortrait = true
                )
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
                TextSearchListBody(
                    modifier = Modifier.weight(1.0f),
                    isPortrait = false
                )
            }
        }
    }
}
