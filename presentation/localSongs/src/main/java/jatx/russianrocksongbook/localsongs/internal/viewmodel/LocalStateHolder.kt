package jatx.russianrocksongbook.localsongs.internal.viewmodel

import jatx.russianrocksongbook.commonviewmodel.AppStateHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
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

    var localStateFlow by Delegates.notNull<StateFlow<LocalState>>()

    fun startMapping(coroutineScope: CoroutineScope) {
        localStateFlow = appStateHolder.appStateFlow.mapNotNull {
            it.customStateMap[localKey] as? LocalState
        }.stateIn(
            coroutineScope,
            SharingStarted.Eagerly,
            LocalState.initial()
        )
    }

    fun changeLocalState(localState: LocalState) =
        appStateHolder.changeCustomState(localKey, localState)

    fun reset() {
        val localState = LocalState.initial()
        changeLocalState(localState)
    }
}