package jatx.russianrocksongbook.start.internal.viewmodel

data class StartUIState(
    val stubCurrentProgress: Int,
    val stubTotalProgress: Int
) {
    companion object {
        fun initial() = StartUIState(
            stubCurrentProgress = 0,
            stubTotalProgress = 100
        )
    }
}
