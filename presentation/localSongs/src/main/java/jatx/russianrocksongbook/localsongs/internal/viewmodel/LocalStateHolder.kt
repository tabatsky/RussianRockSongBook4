package jatx.russianrocksongbook.localsongs.internal.viewmodel

import jatx.russianrocksongbook.commonviewmodel.AppStateHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.properties.Delegates

const val localKey = "local"

@Singleton
class LocalStateHolder @Inject constructor(
    val appStateHolder: AppStateHolder
) {

    private val _localStateFlow = MutableStateFlow(LocalState.initial())
    val localStateFlow = _localStateFlow.asStateFlow()

    fun changeLocalState(localState: LocalState) {
        _localStateFlow.value = localState
    }

    fun reset() {
        val localState = LocalState.initial()
        changeLocalState(localState)
    }
}