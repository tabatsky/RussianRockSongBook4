package jatx.russianrocksongbook.commonview.buttons

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import jatx.dpad.dpadFocusable
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
        modifier = Modifier.
            dpadFocusable(onClick = onBackClick),
        onClick = onBackClick
    )
}