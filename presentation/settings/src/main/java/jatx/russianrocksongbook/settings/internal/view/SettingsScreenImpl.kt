package jatx.russianrocksongbook.settings.internal.view

import androidx.compose.runtime.*
import jatx.russianrocksongbook.settings.internal.viewmodel.SettingsViewModel

@Composable
internal fun SettingsScreenImpl() {
    val settingsViewModel = SettingsViewModel.getInstance()

    val valueTheme = settingsViewModel.valueTheme
    val spinnerStateTheme = settingsViewModel.spinnerStateTheme
    val valueFontScale = settingsViewModel.valueFontScale
    val spinnerStateFontScale = settingsViewModel.spinnerStateFontScale
    val valueOrientation = settingsViewModel.valueOrientation
    val spinnerStateOrientation = settingsViewModel.spinnerStateOrientation
    val valueListenToMusicVariant = settingsViewModel.valueListenToMusicVariant
    val spinnerStateListenToMusicVariant = settingsViewModel.spinnerStateListenToMusicVariant
    val stringScrollSpeed = settingsViewModel.stringScrollSpeed
    val valueScrollSpeed = settingsViewModel.valueScrollSpeed

    val submitAction = settingsViewModel::submitAction

    SettingsScreenImplContent(
        valueTheme = valueTheme,
        spinnerStateTheme = spinnerStateTheme,
        valueFontScale = valueFontScale,
        spinnerStateFontScale = spinnerStateFontScale,
        valueOrientation = valueOrientation,
        spinnerStateOrientation = spinnerStateOrientation,
        valueListenToMusicVariant = valueListenToMusicVariant,
        spinnerStateListenToMusicVariant = spinnerStateListenToMusicVariant,
        stringScrollSpeed = stringScrollSpeed,
        valueScrollSpeed = valueScrollSpeed,
        submitAction = submitAction
    )
}
