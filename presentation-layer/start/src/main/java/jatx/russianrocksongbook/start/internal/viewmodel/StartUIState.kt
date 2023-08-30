package jatx.russianrocksongbook.start.internal.viewmodel

import jatx.russianrocksongbook.commonviewmodel.UIState

data class StartUIState(
    val stubCurrentProgress: Int,
    val stubTotalProgress: Int
): UIState {
    companion object {
        fun initial() = StartUIState(
            stubCurrentProgress = 0,
            stubTotalProgress = 100
        )
    }
}
