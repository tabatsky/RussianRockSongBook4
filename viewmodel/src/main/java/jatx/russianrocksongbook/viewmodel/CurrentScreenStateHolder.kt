package jatx.russianrocksongbook.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentScreenStateHolder @Inject constructor() {
    val currentScreenVariant = MutableStateFlow(CurrentScreenVariant.START)
}