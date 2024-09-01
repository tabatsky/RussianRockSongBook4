package jatx.russianrocksongbook.commonviewmodel

import dagger.hilt.android.scopes.ViewModelScoped
import jatx.russianrocksongbook.domain.repository.preferences.SettingsRepository
import jatx.russianrocksongbook.commonviewmodel.deps.Callbacks
import jatx.russianrocksongbook.commonviewmodel.deps.Resources
import jatx.russianrocksongbook.commonviewmodel.deps.TVDetector
import jatx.russianrocksongbook.commonviewmodel.deps.Toasts
import jatx.russianrocksongbook.domain.usecase.cloud.AddSongToCloudUseCase
import jatx.russianrocksongbook.domain.usecase.cloud.AddWarningUseCase
import javax.inject.Inject

@ViewModelScoped
class CommonViewModelDeps @Inject constructor(
    val settingsRepository: SettingsRepository,
    val callbacks: Callbacks,
    val resources: Resources,
    val toasts: Toasts,
    val tvDetector: TVDetector,
    val addWarningUseCase: AddWarningUseCase,
    val addSongToCloudUseCase: AddSongToCloudUseCase
)