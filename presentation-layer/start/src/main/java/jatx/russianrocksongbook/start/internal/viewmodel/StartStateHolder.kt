package jatx.russianrocksongbook.start.internal.viewmodel

import dagger.hilt.android.scopes.ActivityRetainedScoped
import jatx.russianrocksongbook.viewmodel.CommonStateHolder
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@ActivityRetainedScoped
internal class StartStateHolder @Inject constructor(
    val commonStateHolder: CommonStateHolder
) {
    val stubCurrentProgress = MutableStateFlow(0)
    val stubTotalProgress = MutableStateFlow(100)
}