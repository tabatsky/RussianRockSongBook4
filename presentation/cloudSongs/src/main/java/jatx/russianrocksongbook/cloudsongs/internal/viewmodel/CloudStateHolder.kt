package jatx.russianrocksongbook.cloudsongs.internal.viewmodel

import jatx.russianrocksongbook.commonviewmodel.CommonStateHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CloudStateHolder @Inject constructor(
    val commonStateHolder: CommonStateHolder
) {
    val cloudState = MutableStateFlow(CloudState.initial())

    fun reset() {
        cloudState.update { CloudState.initial() }
    }
}