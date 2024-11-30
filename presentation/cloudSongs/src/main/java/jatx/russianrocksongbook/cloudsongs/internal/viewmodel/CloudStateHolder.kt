package jatx.russianrocksongbook.cloudsongs.internal.viewmodel

import jatx.russianrocksongbook.commonviewmodel.AppStateHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CloudStateHolder @Inject constructor(
    val appStateHolder: AppStateHolder
) {
    private val _cloudStateFlow = MutableStateFlow(CloudState.initial())
    val cloudStateFlow = _cloudStateFlow.asStateFlow()

    fun reset() {
        changeCloudState(CloudState.initial())
    }

    fun changeCloudState(cloudState: CloudState) {
        _cloudStateFlow.value = cloudState
    }
}