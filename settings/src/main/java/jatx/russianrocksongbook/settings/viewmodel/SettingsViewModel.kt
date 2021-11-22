package jatx.russianrocksongbook.settings.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import jatx.russianrocksongbook.viewmodel.MvvmViewModel
import jatx.russianrocksongbook.viewmodel.ScreenStateHolder
import jatx.russianrocksongbook.viewmodel.ViewModelParam
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    viewModelParam: ViewModelParam,
    private val screenStateHolder: ScreenStateHolder
): MvvmViewModel(
    viewModelParam,
    screenStateHolder
) {
    fun restartApp() {
        actions.onRestartApp()
    }
}