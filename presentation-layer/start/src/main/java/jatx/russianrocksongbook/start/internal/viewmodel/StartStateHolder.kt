package jatx.russianrocksongbook.start.internal.viewmodel

import dagger.hilt.android.scopes.ActivityRetainedScoped
import jatx.russianrocksongbook.commonviewmodel.CommonStateHolder
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@ActivityRetainedScoped
internal class StartStateHolder @Inject constructor(
    val commonStateHolder: CommonStateHolder
) {
    val startState = MutableStateFlow(
        StartUIState.initial()
    )
}