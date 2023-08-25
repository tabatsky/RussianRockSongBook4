package jatx.russianrocksongbook.localsongs.api.methods

import jatx.russianrocksongbook.localsongs.internal.viewmodel.ParseAndExecuteVoiceCommand
import jatx.russianrocksongbook.localsongs.internal.viewmodel.VoiceCommandViewModel

fun parseAndExecuteVoiceCommand(cmd: String) {
    VoiceCommandViewModel
        .getStoredInstance()
        ?.submitAction(ParseAndExecuteVoiceCommand(cmd))
}

