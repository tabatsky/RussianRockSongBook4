package jatx.russianrocksongbook.commonviewmodel

import jatx.russianrocksongbook.domain.repository.preferences.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommonStateHolder @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    val commonState = MutableStateFlow(CommonState.initial(settingsRepository.defaultArtist))

    fun reset() {
        commonState.update { CommonState.initial(settingsRepository.defaultArtist) }
    }
}