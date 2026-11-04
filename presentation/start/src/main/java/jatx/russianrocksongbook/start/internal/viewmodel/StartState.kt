package jatx.russianrocksongbook.start.internal.viewmodel

import jatx.russianrocksongbook.commonappstate.UIStateKey
import jatx.russianrocksongbook.commonappstate.UIState

data class StartState(
    val currentProgress: Int,
    val totalProgress: Int
) : UIState {
    companion object {
        fun initial() = StartState(
            currentProgress = 0,
            totalProgress = 100
        )
    }
}

internal val StartStateKey = UIStateKey(StartState::class.java) { StartState.initial() }
