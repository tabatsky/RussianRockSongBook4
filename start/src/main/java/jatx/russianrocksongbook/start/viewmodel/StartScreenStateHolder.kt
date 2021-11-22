package jatx.russianrocksongbook.start.viewmodel

import jatx.russianrocksongbook.viewmodel.ScreenStateHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StartScreenStateHolder @Inject constructor(
    val screenStateHolder: ScreenStateHolder
) {
    val stubCurrentProgress = MutableStateFlow(0)
    val stubTotalProgress = MutableStateFlow(100)
}