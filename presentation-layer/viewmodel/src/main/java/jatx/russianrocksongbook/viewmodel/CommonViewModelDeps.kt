package jatx.russianrocksongbook.viewmodel

import dagger.hilt.android.scopes.ViewModelScoped
import jatx.russianrocksongbook.domain.repository.preferences.SettingsRepository
import jatx.russianrocksongbook.domain.usecase.local.GetArtistsUseCase
import jatx.russianrocksongbook.viewmodel.deps.Callbacks
import jatx.russianrocksongbook.viewmodel.deps.Resources
import jatx.russianrocksongbook.viewmodel.deps.TVDetector
import jatx.russianrocksongbook.viewmodel.deps.Toasts
import javax.inject.Inject

@ViewModelScoped
class CommonViewModelDeps @Inject constructor(
    val settingsRepository: SettingsRepository,
    val callbacks: Callbacks,
    val resources: Resources,
    val toasts: Toasts,
    val tvDetector: TVDetector,
    val getArtistsUseCase: GetArtistsUseCase
)