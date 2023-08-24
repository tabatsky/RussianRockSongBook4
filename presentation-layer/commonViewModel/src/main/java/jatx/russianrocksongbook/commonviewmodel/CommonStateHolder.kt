package jatx.russianrocksongbook.commonviewmodel

import dagger.hilt.android.scopes.ActivityRetainedScoped
import jatx.russianrocksongbook.domain.repository.preferences.SettingsRepository
import jatx.russianrocksongbook.navigation.ScreenVariant
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@ActivityRetainedScoped
class CommonStateHolder @Inject constructor(
    settingsRepository: SettingsRepository
) {
    val currentScreenVariant =
        MutableStateFlow<ScreenVariant>(
            ScreenVariant.Start
        )
    val currentArtist = MutableStateFlow(settingsRepository.defaultArtist)
    val appWasUpdated = MutableStateFlow(false)
    val artistList = MutableStateFlow(listOf<String>())

    val commonState = MutableStateFlow(CommonUIState.initial(settingsRepository.defaultArtist))
}