package jatx.russianrocksongbook.settings.internal.viewmodel

import androidx.compose.runtime.mutableStateOf
import dagger.hilt.android.lifecycle.HiltViewModel
import jatx.russianrocksongbook.viewmodel.CommonStateHolder
import jatx.russianrocksongbook.viewmodel.CommonViewModel
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
    private val settingsRepository =
        commonViewModelDeps.settingsRepository

    val positionDefaultArtist = mutableStateOf(0)
    val isExpandedDefaultArtist = mutableStateOf(false)
    val valueDefaultArtist = mutableStateOf(settingsRepository.defaultArtist)
    val positionFontScale = mutableStateOf(0)
    val isExpandedFontScale = mutableStateOf(false)
    val valueFontScale = mutableStateOf(settingsRepository.commonFontScaleEnum)
    val positionListenToMusicVariant = mutableStateOf(0)
    val isExpandedListenToMusicVariant = mutableStateOf(false)
    val valueListenToMusicVariant = mutableStateOf(settingsRepository.listenToMusicVariant)
    val positionOrientation = mutableStateOf(0)
    val isExpandedOrientation = mutableStateOf(false)
    val valueOrientation = mutableStateOf(settingsRepository.orientation)
    val positionTheme = mutableStateOf(0)
    val isExpandedTheme = mutableStateOf(false)
    val valueTheme = mutableStateOf(settingsRepository.theme)

    val stringScrollSpeed = mutableStateOf(settingsRepository.scrollSpeed.toString())
    val valueScrollSpeed = mutableStateOf(settingsRepository.scrollSpeed)

    fun restartApp() {
        callbacks.onRestartApp()
    }
}