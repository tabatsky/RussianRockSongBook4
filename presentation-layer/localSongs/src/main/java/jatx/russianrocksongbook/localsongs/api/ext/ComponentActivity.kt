package jatx.russianrocksongbook.localsongs.api.ext

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import jatx.russianrocksongbook.localsongs.internal.viewmodel.LocalViewModel
import jatx.russianrocksongbook.localsongs.internal.viewmodel.VoiceCommandViewModel
import jatx.russianrocksongbook.viewmodel.CurrentScreenVariant

fun ComponentActivity.parseAndExecuteVoiceCommand(cmd: String) {
    val voiceCommandViewModel: VoiceCommandViewModel by viewModels()
    voiceCommandViewModel.parseAndExecuteVoiceCommand(cmd)
}

fun ComponentActivity.selectArtist(artist: String) {
    val localViewModel: LocalViewModel by viewModels()
    localViewModel.selectArtist(artist)
}

fun ComponentActivity.selectSongByArtistAndTitle(artist: String, title: String) {
    val localViewModel: LocalViewModel by viewModels()
    localViewModel.selectScreen(
        CurrentScreenVariant
            .SONG_TEXT_BY_ARTIST_AND_TITLE(artist, title)
    )
}
