package jatx.russianrocksongbook.viewmodel

import dagger.hilt.android.scopes.ActivityRetainedScoped
import jatx.russianrocksongbook.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@ActivityRetainedScoped
class CommonStateHolder @Inject constructor(
    settingsRepository: SettingsRepository
) {
    val currentScreenVariant = MutableStateFlow(CurrentScreenVariant.START)
    val currentArtist = MutableStateFlow(settingsRepository.defaultArtist)
    val artistList = MutableStateFlow(listOf<String>())
    val appWasUpdated = MutableStateFlow(false)
}