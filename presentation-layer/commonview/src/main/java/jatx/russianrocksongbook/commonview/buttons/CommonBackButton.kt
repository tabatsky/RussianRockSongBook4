package jatx.russianrocksongbook.commonview.buttons

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import jatx.russianrocksongbook.commonview.R
import jatx.russianrocksongbook.testing.BACK_BUTTON
import jatx.russianrocksongbook.viewmodel.CommonViewModel

@Composable
fun CommonBackButton(
    commonViewModel: CommonViewModel = viewModel()
) {
    val onBackClick = { commonViewModel.back() }
    CommonIconButton(
        resId = R.drawable.ic_back,
        testTag = BACK_BUTTON,
        onClick = onBackClick
    )
}