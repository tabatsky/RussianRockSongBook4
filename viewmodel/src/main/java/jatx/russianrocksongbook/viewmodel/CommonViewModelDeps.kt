package jatx.russianrocksongbook.viewmodel

import dagger.hilt.android.scopes.ViewModelScoped
import jatx.russianrocksongbook.preferences.api.SettingsRepository
import jatx.russianrocksongbook.domain.usecase.GetArtistsUseCase
import jatx.russianrocksongbook.viewmodel.sideeffects.Resources
import jatx.russianrocksongbook.viewmodel.sideeffects.Toasts
import javax.inject.Inject

@ViewModelScoped
class CommonViewModelDeps @Inject constructor(
    val settingsRepository: SettingsRepository,
    val callbacks: Callbacks,
    val resources: Resources,
    val toasts: Toasts,
    val getArtistsUseCase: GetArtistsUseCase
)