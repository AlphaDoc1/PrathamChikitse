package com.example.health.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TTSManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private var pendingText: String? = null

    fun initialize(onReady: (() -> Unit)? = null) {
        if (isInitialized) {
            onReady?.invoke()
            return
        }
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Try the currently active locale first; fall back to en-IN if unavailable.
                val preferred = Locale.getDefault()
                val result = tts?.setLanguage(preferred)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    tts?.language = Locale("en", "IN")
                }
                isInitialized = true
                onReady?.invoke()
                pendingText?.let { speak(it) }
                pendingText = null
            }
        }
    }

    fun speak(text: String) {
        if (!isInitialized) {
            pendingText = text
            initialize()
            return
        }
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "health_tts_${System.currentTimeMillis()}")
    }

    fun speakSteps(steps: List<String>) {
        if (!isInitialized) {
            initialize {
                speakStepsInternal(steps)
            }
            return
        }
        speakStepsInternal(steps)
    }

    private fun speakStepsInternal(steps: List<String>) {
        tts?.let { engine ->
            steps.forEachIndexed { index, step ->
                val utteranceId = "step_$index"
                val mode = if (index == 0) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
                engine.speak("Step ${index + 1}: $step", mode, null, utteranceId)
            }
        }
    }

    fun stop() {
        tts?.stop()
    }

    val isSpeaking: Boolean get() = tts?.isSpeaking == true

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }
}
