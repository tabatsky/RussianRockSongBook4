package jatx.russianrocksongbook.textsearch.internal.viewmodel

import jatx.russianrocksongbook.commonviewmodel.AppStateHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TextSearchStateHolder @Inject constructor(
    val appStateHolder: AppStateHolder
) {
    val textSearchStateFlow = MutableStateFlow(TextSearchState.initial())

    fun reset() {
        textSearchStateFlow.update { TextSearchState.initial() }
    }
}