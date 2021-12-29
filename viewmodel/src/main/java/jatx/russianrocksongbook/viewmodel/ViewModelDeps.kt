package jatx.russianrocksongbook.viewmodel

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import jatx.russianrocksongbook.preferences.api.SettingsRepository
import jatx.russianrocksongbook.domain.usecase.GetArtistsUseCase
import javax.inject.Inject

@ActivityRetainedScoped
open class ViewModelDeps @Inject constructor() {
    @Inject
    lateinit var settingsRepository: SettingsRepository

    @Inject
    lateinit var callbacks: Callbacks

    @ApplicationContext
    @Inject
    lateinit var context: Context

    @Inject
    lateinit var getArtistsUseCase: GetArtistsUseCase
}