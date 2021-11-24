package jatx.russianrocksongbook.start.viewmodel

import jatx.russianrocksongbook.viewmodel.CommonStateHolder
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StartStateHolder @Inject constructor(
    val commonStateHolder: CommonStateHolder
) {
    val stubCurrentProgress = MutableStateFlow(0)
    val stubTotalProgress = MutableStateFlow(100)
}