package jatx.russianrocksongbook.cloudsongs.internal.view.cloudsongtext

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import jatx.russianrocksongbook.cloudsongs.R
import jatx.russianrocksongbook.cloudsongs.internal.viewmodel.CloudViewModel
import jatx.russianrocksongbook.commonview.buttons.CommonIconButton
import jatx.russianrocksongbook.testing.LEFT_BUTTON
import jatx.russianrocksongbook.testing.NUMBER_LABEL
import jatx.russianrocksongbook.testing.RIGHT_BUTTON

@Composable
internal fun CloudSongTextActions(
    position: Int,
    count: Int,
    onCloudSongChanged: () -> Unit
) {
    val cloudViewModel: CloudViewModel = viewModel()

    CommonIconButton(
        resId = R.drawable.ic_left,
        testTag = LEFT_BUTTON
    ) {
        cloudViewModel.prevCloudSong()
        onCloudSongChanged()
    }
    Text(
        modifier = Modifier.testTag(NUMBER_LABEL),
        text = "${position + 1} / $count",
        color = Color.Black,
        fontSize = 20.sp
    )
    CommonIconButton(
        resId = R.drawable.ic_right,
        testTag = RIGHT_BUTTON
    ) {
        cloudViewModel.nextCloudSong()
        onCloudSongChanged()
    }
}
