package jatx.russianrocksongbook.commonviewmodel

import dagger.hilt.android.scopes.ActivityRetainedScoped
import jatx.russianrocksongbook.domain.repository.preferences.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@ActivityRetainedScoped
class CommonStateHolder @Inject constructor(
    settingsRepository: SettingsRepository
) {
    val commonState = MutableStateFlow(CommonState.initial(settingsRepository.defaultArtist))
}