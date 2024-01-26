package jatx.russianrocksongbook.localsongs.internal.viewmodel

import jatx.russianrocksongbook.commonviewmodel.CommonStateHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalStateHolder @Inject constructor(
    val commonStateHolder: CommonStateHolder
) {
    val localState = MutableStateFlow(
        LocalState.initial(commonStateHolder.commonState.value))

    fun reset() {
        localState.update { LocalState.initial(commonStateHolder.commonState.value) }
    }
}