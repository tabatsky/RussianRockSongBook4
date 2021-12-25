package jatx.russianrocksongbook.viewmodel

import dagger.hilt.android.scopes.ActivityRetainedScoped
import jatx.russianrocksongbook.preferences.api.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@ActivityRetainedScoped
class CommonStateHolder @Inject constructor(
    settings: Settings
) {
    val currentScreenVariant = MutableStateFlow(CurrentScreenVariant.START)
    val currentArtist = MutableStateFlow(settings.defaultArtist)
    val artistList = MutableStateFlow(listOf<String>())
    val appWasUpdated = MutableStateFlow(false)
}