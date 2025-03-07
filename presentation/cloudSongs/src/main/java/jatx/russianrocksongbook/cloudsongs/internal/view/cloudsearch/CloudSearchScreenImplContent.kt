package jatx.russianrocksongbook.cloudsongs.internal.view.cloudsearch

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.paging.compose.LazyPagingItems
import jatx.russianrocksongbook.cloudsongs.R
import jatx.russianrocksongbook.cloudsongs.internal.viewmodel.PerformCloudSearch
import jatx.russianrocksongbook.cloudsongs.internal.viewmodel.SearchState
import jatx.russianrocksongbook.cloudsongs.internal.viewmodel.UpdateCloudSongListNeedScroll
import jatx.russianrocksongbook.commonview.appbar.CommonSideAppBar
import jatx.russianrocksongbook.commonview.appbar.CommonTopAppBar
import jatx.russianrocksongbook.commonview.theme.LocalAppTheme
import jatx.russianrocksongbook.commonviewmodel.UIAction
import jatx.russianrocksongbook.domain.models.cloud.CloudSong
import jatx.russianrocksongbook.domain.repository.cloud.CloudSearchOrderBy
import jatx.russianrocksongbook.testing.APP_BAR_TITLE
import jatx.spinner.SpinnerState

@Composable
internal fun CloudSearchScreenImplContent(
    randomKey: Int,
    isBackFromSong: Boolean,
    searchState: SearchState,
    needScroll: Boolean,
    scrollPosition: Int,
    cloudSongItems: LazyPagingItems<CloudSong>?,
    searchFor: String,
    orderBy: CloudSearchOrderBy,
    spinnerStateOrderBy: MutableState<SpinnerState>,
    submitAction: (UIAction) -> Unit
) {
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
                submitAction(UpdateCloudSongListNeedScroll(true))
            }
        }

        if (randomKeyChanged) {
            savedRandomKey = randomKey
            LaunchedEffect(Unit) {
                if (!isBackFromSong && !wasOrientationChanged) {
                    submitAction(PerformCloudSearch("", CloudSearchOrderBy.BY_ID_DESC))
                }
            }
        }

        LaunchedEffect(Unit) {
            if (isBackFromSong) {
                submitAction(UpdateCloudSongListNeedScroll(true))
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
                    title = stringResource(id = R.string.title_activity_cloud_search),
                    titleTestTag = APP_BAR_TITLE
                )
                CloudSearchBody(
                    modifier = Modifier.weight(1.0f),
                    isPortrait = true,
                    searchState = searchState,
                    needScroll = needScroll,
                    scrollPosition = scrollPosition,
                    cloudSongItems = cloudSongItems,
                    searchFor = searchFor,
                    orderBy = orderBy,
                    spinnerStateOrderBy = spinnerStateOrderBy,
                    submitAction = submitAction
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(theme.colorBg)
            ) {
                CommonSideAppBar(
                    title = stringResource(id = R.string.title_activity_cloud_search),
                    titleTestTag = APP_BAR_TITLE
                )
                CloudSearchBody(
                    modifier = Modifier.weight(1.0f),
                    isPortrait = false,
                    searchState = searchState,
                    needScroll = needScroll,
                    scrollPosition = scrollPosition,
                    cloudSongItems = cloudSongItems,
                    searchFor = searchFor,
                    orderBy = orderBy,
                    spinnerStateOrderBy = spinnerStateOrderBy,
                    submitAction = submitAction
                )
            }
        }
    }
}