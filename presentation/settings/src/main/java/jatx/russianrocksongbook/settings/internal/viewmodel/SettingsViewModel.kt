package jatx.russianrocksongbook.settings.internal.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jatx.russianrocksongbook.commonviewmodel.AppStateHolder
import jatx.russianrocksongbook.commonviewmodel.CommonViewModel
import jatx.russianrocksongbook.commonviewmodel.CommonViewModelDeps
import jatx.russianrocksongbook.commonviewmodel.UIAction
import jatx.russianrocksongbook.domain.repository.preferences.ListenToMusicVariant
import jatx.russianrocksongbook.domain.repository.preferences.Orientation
import jatx.russianrocksongbook.domain.repository.preferences.Theme
import jatx.spinner.SpinnerState
import javax.inject.Inject

@HiltViewModel
internal class SettingsViewModel @Inject constructor(
    appStateHolder: AppStateHolder,
    commonViewModelDeps: CommonViewModelDeps
): CommonViewModel(
    appStateHolder,
    commonViewModelDeps
) {
    companion object {
        private const val key = "Settings"

        @Composable
        fun getInstance(): SettingsViewModel {
            storage[key] = hiltViewModel<SettingsViewModel>()
            storage[key]?.launchJobsIfNecessary()
            return storage[key] as SettingsViewModel
        }
    }

    val spinnerStateFontScale = mutableStateOf(SpinnerState(0, false))
    val valueFontScale = mutableStateOf(settings.commonFontScaleEnum)
    val spinnerStateListenToMusicVariant = mutableStateOf(SpinnerState(0, false))
    val valueListenToMusicVariant = mutableStateOf(settings.listenToMusicVariant)
    val spinnerStateOrientation = mutableStateOf(SpinnerState(0, false))
    val valueOrientation = mutableStateOf(settings.orientation)
    val spinnerStateTheme = mutableStateOf(SpinnerState(0, false))
    val valueTheme = mutableStateOf(settings.theme)

    val stringScrollSpeed = mutableStateOf(settings.scrollSpeed.toString())
    val valueScrollSpeed = mutableFloatStateOf(settings.scrollSpeed)

    override fun handleAction(action: UIAction) {
        when (action) {
            is SaveSettings -> with(action) {
                saveSettings(
                    theme,
                    fontScale,
                    orientation,
                    listenToMusicVariant,
                    scrollSpeed
                )
            }
            is ApplySettings -> applySettings()
            else -> super.handleAction(action)
        }
    }

    private fun saveSettings(
        theme: Theme,
        fontScale: Float,
        orientation: Orientation,
        listenToMusicVariant: ListenToMusicVariant,
        scrollSpeed: Float) {

        settings.theme = theme
        settings.commonFontScale = fontScale
        settings.orientation = orientation
        settings.listenToMusicVariant = listenToMusicVariant
        settings.scrollSpeed = scrollSpeed
    }

    private fun applySettings() {
        reloadSettings()
        callbacks.onApplyOrientation()
    }
}