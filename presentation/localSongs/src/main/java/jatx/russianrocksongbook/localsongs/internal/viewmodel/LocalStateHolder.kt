package jatx.russianrocksongbook.localsongs.internal.viewmodel

import dagger.hilt.android.scopes.ViewModelScoped
import jatx.russianrocksongbook.commonsongtext.viewmodel.CommonSongTextStateHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@ViewModelScoped
class LocalStateHolder @Inject constructor(
    val commonSongTextStateHolder: CommonSongTextStateHolder
) {

    private val _localStateFlow = MutableStateFlow(LocalState.initial())
    val localStateFlow = _localStateFlow.asStateFlow()

    fun changeLocalState(localState: LocalState) {
        _localStateFlow.value = localState
    }

    fun reset() {
        commonSongTextStateHolder.reset()
        val localState = LocalState.initial()
        changeLocalState(localState)
    }
}