package jatx.russianrocksongbook.helpers

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContract
import dagger.hilt.android.scopes.ActivityScoped
import java.util.*
import javax.inject.Inject

@ActivityScoped
class VoiceCommandHelper @Inject constructor(
    activity: Activity
) {
    private var onVoiceCommand: (String) -> Unit = {}

    private val speechRecognizeLauncher =
        (activity as? ComponentActivity)?.registerForActivityResult(
            SpeechRecognizeContract()
        ) {
            it?.apply {
                onVoiceCommand(this)
            }
        }

    fun recognizeVoiceCommand(onVoiceCommand: (String) -> Unit) {
        this.onVoiceCommand = onVoiceCommand
        speechRecognizeLauncher?.launch(null)
    }
}

class SpeechRecognizeContract: ActivityResultContract<Any?, String?>() {
    override fun createIntent(context: Context, input: Any?) =
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.forLanguageTag("ru-RU"))
        }

    override fun parseResult(resultCode: Int, intent: Intent?): String? {
        if (resultCode != Activity.RESULT_OK) {
            return null
        }
        return intent?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.getOrNull(0)
    }
}
