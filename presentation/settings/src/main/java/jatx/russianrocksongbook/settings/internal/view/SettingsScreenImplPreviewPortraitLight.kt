package jatx.russianrocksongbook.settings.internal.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import jatx.russianrocksongbook.commonview.theme.LightTheme
import jatx.russianrocksongbook.domain.repository.preferences.FontScale
import jatx.russianrocksongbook.domain.repository.preferences.ListenToMusicVariant
import jatx.russianrocksongbook.domain.repository.preferences.Orientation
import jatx.russianrocksongbook.domain.repository.preferences.Theme
import jatx.spinner.SpinnerState

@Preview
@Composable
fun SettingsScreenImplPreviewPortraitLight() {
    val artistList = (1..30)
        .map { "Исполнитель $it" }

    val valueTheme = remember { mutableStateOf(Theme.entries[0]) }
    val spinnerStateTheme = remember { mutableStateOf(SpinnerState(0, false)) }
    val valueFontScale = remember { mutableStateOf(FontScale.entries[0]) }
    val spinnerStateFontScale = remember { mutableStateOf(SpinnerState(0, false)) }
    val valueDefaultArtist = remember { mutableStateOf("Исполнитель 1") }
    val spinnerStateDefaultArtist = remember { mutableStateOf(SpinnerState(0, false)) }
    val valueOrientation = remember { mutableStateOf(Orientation.entries[0]) }
    val spinnerStateOrientation = remember { mutableStateOf(SpinnerState(0, false)) }
    val valueListenToMusicVariant = remember { mutableStateOf(ListenToMusicVariant.entries[0]) }
    val spinnerStateListenToMusicVariant = remember { mutableStateOf(SpinnerState(0, false)) }
    val stringScrollSpeed = remember { mutableStateOf("1.0") }
    val valueScrollSpeed = remember { mutableFloatStateOf(1.0f) }

    LightTheme {
        SettingsScreenImplContent(
            artistList = artistList,
            valueTheme = valueTheme,
            spinnerStateTheme = spinnerStateTheme,
            valueFontScale = valueFontScale,
            spinnerStateFontScale = spinnerStateFontScale,
            valueDefaultArtist = valueDefaultArtist,
            spinnerStateDefaultArtist = spinnerStateDefaultArtist,
            valueOrientation = valueOrientation,
            spinnerStateOrientation = spinnerStateOrientation,
            valueListenToMusicVariant = valueListenToMusicVariant,
            spinnerStateListenToMusicVariant = spinnerStateListenToMusicVariant,
            stringScrollSpeed = stringScrollSpeed,
            valueScrollSpeed = valueScrollSpeed,
            submitAction = {}
        )
    }
}