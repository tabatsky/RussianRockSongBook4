package jatx.russianrocksongbook.cloudsongs.internal.view.cloudsearch

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import jatx.russianrocksongbook.cloudsongs.R
import jatx.russianrocksongbook.cloudsongs.internal.viewmodel.CloudViewModel
import jatx.russianrocksongbook.commonview.appbar.CommonSideAppBar
import jatx.russianrocksongbook.commonview.appbar.CommonTopAppBar

@Composable
internal fun CloudSearchScreenImpl() {
    val cloudViewModel: CloudViewModel = viewModel()
    val theme = cloudViewModel.settings.theme

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val W = this.maxWidth
        val H = this.maxHeight

        if (W < H) {
            val isPortrait = true
            val isLastOrientationPortrait by cloudViewModel
                .isLastOrientationPortrait.collectAsState()
            cloudViewModel.updateOrientationWasChanged(
                isPortrait != isLastOrientationPortrait
            )
            cloudViewModel.updateLastOrientationIsPortrait(true)

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
            val isPortrait = false
            val isLastOrientationPortrait by cloudViewModel
                .isLastOrientationPortrait.collectAsState()
            cloudViewModel.updateOrientationWasChanged(
                isPortrait != isLastOrientationPortrait
            )
            cloudViewModel.updateLastOrientationIsPortrait(false)

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