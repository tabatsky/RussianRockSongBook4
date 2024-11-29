package jatx.russianrocksongbook.textsearch.internal.viewmodel

import dagger.hilt.android.scopes.ViewModelScoped
import jatx.russianrocksongbook.commonsongtext.viewmodel.CommonSongTextStateHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@ViewModelScoped
class TextSearchStateHolder @Inject constructor(
    val commonSongTextStateHolder: CommonSongTextStateHolder
) {
    private val _textSearchStateFlow = MutableStateFlow(TextSearchState.initial())
    val textSearchStateFlow = _textSearchStateFlow.asStateFlow()

    fun changeTextSearchState(newState: TextSearchState) {
        _textSearchStateFlow.value = newState
    }

    fun reset() {
        commonSongTextStateHolder.reset()
        _textSearchStateFlow.update { TextSearchState.initial() }
    }
}