package jatx.russianrocksongbook.settings.internal.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import jatx.russianrocksongbook.viewmodel.MvvmViewModel
import jatx.russianrocksongbook.viewmodel.CommonStateHolder
import jatx.russianrocksongbook.viewmodel.ViewModelDeps
import javax.inject.Inject

@HiltViewModel
internal class SettingsViewModel @Inject constructor(
    viewModelDeps: ViewModelDeps,
    commonStateHolder: CommonStateHolder
): MvvmViewModel(
    viewModelDeps,
    commonStateHolder
) {
    fun restartApp() {
        callbacks.onRestartApp()
    }
}