package com.example.voiceaccess.speech

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer

/** Small wrapper around Android's built-in SpeechRecognizer. */
class VoiceRecognizer(
    context: Context,
    private val onResult: (String) -> Unit,
    private val onStatus: (String) -> Unit,
) : RecognitionListener {
    private val appContext = context.applicationContext
    private val recognizer = SpeechRecognizer.createSpeechRecognizer(appContext)

    init {
        recognizer.setRecognitionListener(this)
    }

    fun startListening() {
        if (!SpeechRecognizer.isRecognitionAvailable(appContext)) {
            onStatus("Speech recognition is not available on this phone.")
            return
        }
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Say a VoiceAccess command")
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
        recognizer.startListening(intent)
    }

    fun destroy() = recognizer.destroy()

    override fun onReadyForSpeech(params: Bundle?) = onStatus("Listening…")
    override fun onBeginningOfSpeech() = onStatus("Listening…")
    override fun onRmsChanged(rmsdB: Float) = Unit
    override fun onBufferReceived(buffer: ByteArray?) = Unit
    override fun onEndOfSpeech() = onStatus("Processing…")
    override fun onError(error: Int) = onStatus("Speech recognition error: ${errorMessage(error)}")
    override fun onResults(results: Bundle?) {
        val text = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
        if (text == null) onStatus("I did not hear a command. Please try again.") else onResult(text)
    }
    override fun onPartialResults(partialResults: Bundle?) = Unit
    override fun onEvent(eventType: Int, params: Bundle?) = Unit

    private fun errorMessage(error: Int): String = when (error) {
        SpeechRecognizer.ERROR_AUDIO -> "audio problem"
        SpeechRecognizer.ERROR_CLIENT -> "client problem"
        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "microphone permission is missing"
        SpeechRecognizer.ERROR_NETWORK, SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "network problem"
        SpeechRecognizer.ERROR_NO_MATCH -> "no matching speech"
        SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "recognizer is busy"
        SpeechRecognizer.ERROR_SERVER -> "server problem"
        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "speech timed out"
        else -> "unknown problem"
    }

}
