package jatx.russianrocksongbook.start.internal.viewmodel

data class StartState(
    val currentProgress: Int,
    val totalProgress: Int
) {
    companion object {
        fun initial() = StartState(
            currentProgress = 0,
            totalProgress = 100
        )
    }
}
