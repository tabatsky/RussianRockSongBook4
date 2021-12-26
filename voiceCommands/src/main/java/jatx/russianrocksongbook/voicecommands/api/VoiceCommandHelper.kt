package jatx.russianrocksongbook.voicecommands.api

interface VoiceCommandHelper {
    fun recognizeVoiceCommand(onVoiceCommand: (String) -> Unit)
}
