package jatx.russianrocksongbook.localsongs.internal.viewmodel

import dagger.hilt.android.scopes.ActivityRetainedScoped
import jatx.russianrocksongbook.commonviewmodel.CommonStateHolder
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@ActivityRetainedScoped
internal class LocalStateHolder @Inject constructor(
    val commonStateHolder: CommonStateHolder
) {
    val localState = MutableStateFlow(
        LocalUIState.initial(commonStateHolder.commonState.value))
}