package jatx.russianrocksongbook.cloudsongs.internal.viewmodel

import jatx.russianrocksongbook.commonviewmodel.AppStateHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CloudStateHolder @Inject constructor(
    val appStateHolder: AppStateHolder
) {
    val cloudStateFlow = MutableStateFlow(CloudState.initial())

    fun reset() {
        cloudStateFlow.update { CloudState.initial() }
    }
}