package jatx.russianrocksongbook.commonviewmodel

import jatx.russianrocksongbook.domain.repository.preferences.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppStateHolder @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    private val _appStateFlow = MutableStateFlow(AppState.initial(settingsRepository.defaultArtist))

    val appStateFlow = _appStateFlow.asStateFlow()

    fun reset() {
        _appStateFlow.value = AppState.initial(settingsRepository.defaultArtist)
    }

    fun changeAppState(appState: AppState) {
        _appStateFlow.value = appState
    }

    fun changeCustomState(key: String, customState: CustomState) {
        _appStateFlow.value =
            _appStateFlow.value.changeCustomState(key, customState)
    }
}