package jatx.russianrocksongbook.localsongs.api.ext

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import jatx.russianrocksongbook.localsongs.internal.viewmodel.VoiceCommandViewModel

fun ComponentActivity.parseAndExecuteVoiceCommand(cmd: String) {
    val voiceCommandViewModel: VoiceCommandViewModel by viewModels()
    voiceCommandViewModel.parseAndExecuteVoiceCommand(cmd)
}

