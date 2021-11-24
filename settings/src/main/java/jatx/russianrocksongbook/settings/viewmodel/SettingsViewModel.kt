package jatx.russianrocksongbook.settings.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import jatx.russianrocksongbook.viewmodel.MvvmViewModel
import jatx.russianrocksongbook.viewmodel.CommonStateHolder
import jatx.russianrocksongbook.viewmodel.ViewModelParam
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    viewModelParam: ViewModelParam,
    commonStateHolder: CommonStateHolder
): MvvmViewModel(
    viewModelParam,
    commonStateHolder
) {
    fun restartApp() {
        callbacks.onRestartApp()
    }
}