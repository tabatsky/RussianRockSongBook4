package jatx.russianrocksongbook.settings.internal.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jatx.russianrocksongbook.commonview.spinner.SpinnerState
import jatx.russianrocksongbook.commonviewmodel.CommonStateHolder
import jatx.russianrocksongbook.commonviewmodel.CommonViewModel
import jatx.russianrocksongbook.commonviewmodel.CommonViewModelDeps
import jatx.russianrocksongbook.commonviewmodel.UIAction
import jatx.russianrocksongbook.domain.repository.preferences.ListenToMusicVariant
import jatx.russianrocksongbook.domain.repository.preferences.Orientation
import jatx.russianrocksongbook.domain.repository.preferences.Theme
import javax.inject.Inject

@HiltViewModel
internal class SettingsViewModel @Inject constructor(
    commonStateHolder: CommonStateHolder,
    commonViewModelDeps: CommonViewModelDeps
): CommonViewModel(
    commonStateHolder,
    commonViewModelDeps
) {
    val spinnerStateDefaultArtist = mutableStateOf(SpinnerState(0, false))
    val valueDefaultArtist = mutableStateOf(settings.defaultArtist)
    val spinnerStateFontScale = mutableStateOf(SpinnerState(0, false))
    val valueFontScale = mutableStateOf(settings.commonFontScaleEnum)
    val spinnerStateListenToMusicVariant = mutableStateOf(SpinnerState(0, false))
    val valueListenToMusicVariant = mutableStateOf(settings.listenToMusicVariant)
    val spinnerStateOrientation = mutableStateOf(SpinnerState(0, false))
    val valueOrientation = mutableStateOf(settings.orientation)
    val spinnerStateTheme = mutableStateOf(SpinnerState(0, false))
    val valueTheme = mutableStateOf(settings.theme)

    val stringScrollSpeed = mutableStateOf(settings.scrollSpeed.toString())
    val valueScrollSpeed = mutableStateOf(settings.scrollSpeed)

    companion object {
        private const val key = "Settings"

        @Composable
        fun getInstance(): SettingsViewModel {
            storage[key] = hiltViewModel<SettingsViewModel>()
            storage[key]?.launchJobsIfNecessary()
            return storage[key] as SettingsViewModel
        }
    }

    override fun handleAction(action: UIAction) {
        when (action) {
            is SaveSettings -> with(action) {
                saveSettings(
                    theme,
                    fontScale,
                    defaultArtist,
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
        defaultArtist: String,
        orientation: Orientation,
        listenToMusicVariant: ListenToMusicVariant,
        scrollSpeed: Float) {

        settings.theme = theme
        settings.commonFontScale = fontScale
        settings.defaultArtist = defaultArtist
        settings.orientation = orientation
        settings.listenToMusicVariant = listenToMusicVariant
        settings.scrollSpeed = scrollSpeed
    }

    private fun applySettings() {
        reloadSettings()
        callbacks.onApplyOrientation()
    }
}