package jatx.russianrocksongbook.voicecommands.internal.impl

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContract
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import it.czerwinski.android.hilt.annotations.BoundTo
import jatx.russianrocksongbook.voicecommands.api.VoiceCommandHelper
import java.util.*
import javax.inject.Inject

@ActivityScoped
@BoundTo(supertype = VoiceCommandHelper::class, component = ActivityComponent::class)
internal class VoiceCommandHelperImpl @Inject constructor(
    activity: Activity
): VoiceCommandHelper {
    private var onVoiceCommand: (String) -> Unit = {}

    private val speechRecognizeLauncher =
        (activity as? ComponentActivity)?.registerForActivityResult(
            SpeechRecognizeContract()
        ) {
            it?.apply {
                onVoiceCommand(this)
            }
        }

    override fun recognizeVoiceCommand(
        onVoiceCommand: (String) -> Unit,
        onError: () -> Unit
    ) {
        this.onVoiceCommand = onVoiceCommand
        try {
            speechRecognizeLauncher?.launch(null)
        } catch (e: ActivityNotFoundException) {
            onError()
        }
    }
}

private class SpeechRecognizeContract: ActivityResultContract<Any?, String?>() {
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
