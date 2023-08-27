package jatx.russianrocksongbook.cloudsongs.internal.viewmodel

import dagger.hilt.android.scopes.ActivityRetainedScoped
import jatx.russianrocksongbook.commonviewmodel.CommonStateHolder
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@ActivityRetainedScoped
internal class CloudStateHolder @Inject constructor(
    val commonStateHolder: CommonStateHolder
) {
    val cloudState = MutableStateFlow(
        CloudUIState.initial(commonStateHolder.commonState.value))
}