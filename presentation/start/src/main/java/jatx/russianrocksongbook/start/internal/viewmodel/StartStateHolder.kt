package jatx.russianrocksongbook.start.internal.viewmodel

import dagger.hilt.android.scopes.ActivityRetainedScoped
import jatx.russianrocksongbook.commonviewmodel.AppStateHolder
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@ActivityRetainedScoped
internal class StartStateHolder @Inject constructor(
    val appStateHolder: AppStateHolder
) {
    val startStateFlow = MutableStateFlow(
        StartState.initial()
    )
}