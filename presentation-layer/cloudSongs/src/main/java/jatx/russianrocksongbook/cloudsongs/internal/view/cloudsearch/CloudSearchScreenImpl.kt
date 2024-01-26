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
import jatx.russianrocksongbook.cloudsongs.R
import jatx.russianrocksongbook.cloudsongs.internal.viewmodel.PerformCloudSearch
import jatx.russianrocksongbook.cloudsongs.internal.viewmodel.CloudViewModel
import jatx.russianrocksongbook.cloudsongs.internal.viewmodel.UpdateNeedScroll
import jatx.russianrocksongbook.commonview.appbar.CommonSideAppBar
import jatx.russianrocksongbook.commonview.appbar.CommonTopAppBar
import jatx.russianrocksongbook.domain.repository.cloud.OrderBy

@Composable
internal fun CloudSearchScreenImpl(randomKey: Int, isBackFromSong: Boolean) {
    val cloudViewModel = CloudViewModel.getInstance()

    val theme = cloudViewModel.theme.collectAsState().value

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
                cloudViewModel.submitAction(UpdateNeedScroll(true))
            }
        }

        if (randomKeyChanged) {
            savedRandomKey = randomKey
            LaunchedEffect(Unit) {
                if (!isBackFromSong && !wasOrientationChanged) {
                    cloudViewModel.submitAction(PerformCloudSearch("", OrderBy.BY_ID_DESC))
                }
            }
        }

        LaunchedEffect(Unit) {
            if (isBackFromSong) {
                cloudViewModel.submitAction(UpdateNeedScroll(true))
            }
        }

        if (wasOrientationChanged) return@BoxWithConstraints

        if (W < H) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(theme.colorBg)
            ) {
                CommonTopAppBar(title = stringResource(id = R.string.title_activity_cloud_search))
                CloudSearchBody(
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
                CommonSideAppBar(title = stringResource(id = R.string.title_activity_cloud_search))
                CloudSearchBody(
                    modifier = Modifier.weight(1.0f),
                    isPortrait = false
                )
            }
        }
    }
}