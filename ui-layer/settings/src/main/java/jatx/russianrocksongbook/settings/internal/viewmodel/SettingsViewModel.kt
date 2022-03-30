package jatx.russianrocksongbook.settings.internal.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import jatx.russianrocksongbook.viewmodel.CommonViewModel
import jatx.russianrocksongbook.viewmodel.CommonStateHolder
import jatx.russianrocksongbook.viewmodel.CommonViewModelDeps
import javax.inject.Inject

@HiltViewModel
internal class SettingsViewModel @Inject constructor(
    commonStateHolder: CommonStateHolder,
    commonViewModelDeps: CommonViewModelDeps
): CommonViewModel(
    commonStateHolder,
    commonViewModelDeps
) {
    fun restartApp() {
        callbacks.onRestartApp()
    }
}