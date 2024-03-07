package jatx.russianrocksongbook.start.internal.viewmodel

data class StartState(
    val stubCurrentProgress: Int,
    val stubTotalProgress: Int
) {
    companion object {
        fun initial() = StartState(
            stubCurrentProgress = 0,
            stubTotalProgress = 100
        )
    }
}
