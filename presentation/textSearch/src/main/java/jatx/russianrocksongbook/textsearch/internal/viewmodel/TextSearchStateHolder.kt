package jatx.russianrocksongbook.textsearch.internal.viewmodel

import jatx.russianrocksongbook.commonviewmodel.AppStateHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TextSearchStateHolder @Inject constructor(
    val appStateHolder: AppStateHolder
) {
    private val _textSearchStateFlow = MutableStateFlow(TextSearchState.initial())
    val textSearchStateFlow = _textSearchStateFlow.asStateFlow()

    fun changeTextSearchState(newState: TextSearchState) {
        _textSearchStateFlow.value = newState
    }

    fun reset() {
        _textSearchStateFlow.update { TextSearchState.initial() }
    }
}