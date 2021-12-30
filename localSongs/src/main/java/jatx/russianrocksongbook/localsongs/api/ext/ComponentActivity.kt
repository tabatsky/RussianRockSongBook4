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

fun ComponentActivity.selectArtist(artist: String) = runOnUiThread {
    val localViewModel: LocalViewModel by viewModels()
    localViewModel.selectArtist(artist)
}

fun ComponentActivity.selectSongByArtistAndTitle(artist: String, title: String) =
    runOnUiThread {
        val localViewModel: LocalViewModel by viewModels()
        localViewModel.selectArtist(
            artist = artist,
            forceOnSuccess = true,
            onSuccess = {
                val position = localViewModel
                    .currentSongList
                    .value
                    .map { it.title }
                    .indexOf(title)
                localViewModel.selectSong(position)
                localViewModel.selectScreen(CurrentScreenVariant.SONG_TEXT)
            }
        )
    }