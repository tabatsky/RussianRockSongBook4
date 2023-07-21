package jatx.russianrocksongbook.settings.internal.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jatx.russianrocksongbook.commonview.spinner.SpinnerState
import jatx.russianrocksongbook.commonviewmodel.CommonStateHolder
import jatx.russianrocksongbook.commonviewmodel.CommonViewModel
import jatx.russianrocksongbook.commonviewmodel.CommonViewModelDeps
import javax.inject.Inject

@HiltViewModel
internal class SettingsViewModel @Inject constructor(
    commonStateHolder: CommonStateHolder,
    commonViewModelDeps: CommonViewModelDeps
): CommonViewModel(
    commonStateHolder,
    commonViewModelDeps
) {
    private val settingsRepository =
        commonViewModelDeps.settingsRepository

    val spinnerStateDefaultArtist = mutableStateOf(SpinnerState(0, false))
    val valueDefaultArtist = mutableStateOf(settingsRepository.defaultArtist)
    val spinnerStateFontScale = mutableStateOf(SpinnerState(0, false))
    val valueFontScale = mutableStateOf(settingsRepository.commonFontScaleEnum)
    val spinnerStateListenToMusicVariant = mutableStateOf(SpinnerState(0, false))
    val valueListenToMusicVariant = mutableStateOf(settingsRepository.listenToMusicVariant)
    val spinnerStateOrientation = mutableStateOf(SpinnerState(0, false))
    val valueOrientation = mutableStateOf(settingsRepository.orientation)
    val spinnerStateTheme = mutableStateOf(SpinnerState(0, false))
    val valueTheme = mutableStateOf(settingsRepository.theme)

    val stringScrollSpeed = mutableStateOf(settingsRepository.scrollSpeed.toString())
    val valueScrollSpeed = mutableStateOf(settingsRepository.scrollSpeed)

    companion object {
        private const val key = "Settings"

        @Composable
        fun getInstance(): SettingsViewModel {
            if (!storage.containsKey(key)){
                storage[key] = hiltViewModel<SettingsViewModel>()
            }
            return storage[key] as SettingsViewModel
        }
    }

    fun restartApp() {
        callbacks.onRestartApp()
    }
}