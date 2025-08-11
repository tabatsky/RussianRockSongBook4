package jatx.russianrocksongbook.cloudsongs.internal.view.cloudsearch

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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

@SuppressLint("ConfigurationScreenWidthHeight")
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

    val actualRandomKey = if (isBackFromSong) randomKey else 0
    var savedRandomKey by rememberSaveable { mutableIntStateOf(actualRandomKey) }
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

        if (wasOrientationChanged) {
            LaunchedEffect(Unit) {
                isLastOrientationPortrait = isPortrait
                submitAction(UpdateCloudSongListNeedScroll(true))
            }
        }

        if (randomKeyChanged) {
            LaunchedEffect(Unit) {
                savedRandomKey = randomKey
                if (!isBackFromSong && !wasOrientationChanged) {
                    submitAction(PerformCloudSearch("", CloudSearchOrderBy.BY_ID_DESC))
                }
            }
        }

        if (isBackFromSong) {
            LaunchedEffect(Unit) {
                submitAction(UpdateCloudSongListNeedScroll(true))
            }
        }

        if (wasOrientationChanged) return@Box

        @Composable
        fun TheBody(modifier: Modifier) {
            CloudSearchBody(
                modifier = modifier,
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
                TheBody(
                    modifier = Modifier.weight(1.0f)
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
                TheBody(
                    modifier = Modifier.weight(1.0f)
                )
            }
        }
    }
}