package jatx.russianrocksongbook.commonsongtext.viewmodel

import dagger.hilt.android.scopes.ViewModelScoped
import jatx.russianrocksongbook.commonviewmodel.AppStateHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@ViewModelScoped
class CommonSongTextStateHolder @Inject constructor(
    val appStateHolder: AppStateHolder
) {

    private val _commonSongTextStateFlow = MutableStateFlow(CommonSongTextState.initial())
    val commonSongTextStateFlow = _commonSongTextStateFlow.asStateFlow()

    fun changeCommonSongTextState(localState: CommonSongTextState) {
        _commonSongTextStateFlow.value = localState
    }

    fun reset() {
        val localState = CommonSongTextState.initial()
        changeCommonSongTextState(localState)
    }
}