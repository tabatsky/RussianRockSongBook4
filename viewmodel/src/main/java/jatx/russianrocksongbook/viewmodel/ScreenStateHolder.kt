package jatx.russianrocksongbook.viewmodel

import jatx.russianrocksongbook.model.preferences.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScreenStateHolder @Inject constructor(
    settings: Settings
) {
    val currentScreenVariant = MutableStateFlow(CurrentScreenVariant.START)
    val currentArtist = MutableStateFlow(settings.defaultArtist)
    val artistList = MutableStateFlow(listOf<String>())
}