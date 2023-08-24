package jatx.russianrocksongbook.commonview.buttons

import androidx.compose.runtime.Composable
import jatx.russianrocksongbook.commonview.R
import jatx.russianrocksongbook.commonviewmodel.Back
import jatx.russianrocksongbook.testing.BACK_BUTTON
import jatx.russianrocksongbook.commonviewmodel.CommonViewModel

@Composable
fun CommonBackButton(
    commonViewModel: CommonViewModel = CommonViewModel.getInstance()
) {
    val onBackClick = { commonViewModel.submitAction(Back) }
    CommonIconButton(
        resId = R.drawable.ic_back,
        testTag = BACK_BUTTON,
        onClick = onBackClick
    )
}