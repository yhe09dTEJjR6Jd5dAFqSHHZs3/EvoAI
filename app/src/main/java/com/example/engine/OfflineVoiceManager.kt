package com.example.engine

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class OfflineVoiceManager(private val context: Context) {

    private var speechRecognizer: SpeechRecognizer? = null
    private var textToSpeech: TextToSpeech? = null
    private var isTtsInitialized = false

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _rmsLevel = MutableStateFlow(0f)
    val rmsLevel: StateFlow<Float> = _rmsLevel.asStateFlow()

    private val _recognizedText = MutableStateFlow("")
    val recognizedText: StateFlow<String> = _recognizedText.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    var onSpeechResultReceived: ((String) -> Unit)? = null
    var onSpeechStarted: (() -> Unit)? = null
    var onSpeechEnded: (() -> Unit)? = null

    init {
        initTts()
    }

    private fun initTts() {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isTtsInitialized = true
                val currentLocale = Locale.getDefault()
                val langResult = textToSpeech?.setLanguage(currentLocale)
                if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                    textToSpeech?.language = Locale.US
                }
                textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _isSpeaking.value = true
                    }

                    override fun onDone(utteranceId: String?) {
                        _isSpeaking.value = false
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        _isSpeaking.value = false
                    }
                })
            }
        }
    }

    fun startListening(isChinese: Boolean = false) {
        if (_isSpeaking.value) {
            stopSpeaking()
        }

        try {
            if (speechRecognizer == null) {
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
            }

            speechRecognizer?.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    _isListening.value = true
                    _errorMessage.value = null
                    onSpeechStarted?.invoke()
                }

                override fun onBeginningOfSpeech() {
                    _isListening.value = true
                }

                override fun onRmsChanged(rmsdB: Float) {
                    // Normalize RMS dB (typical range -2 to 10 dB) to 0.0 - 1.0
                    val normalized = ((rmsdB + 2f) / 12f).coerceIn(0.05f, 1f)
                    _rmsLevel.value = normalized
                }

                override fun onBufferReceived(buffer: ByteArray?) {}

                override fun onEndOfSpeech() {
                    _isListening.value = false
                    _rmsLevel.value = 0f
                    onSpeechEnded?.invoke()
                }

                override fun onError(error: Int) {
                    _isListening.value = false
                    _rmsLevel.value = 0f
                    val msg = when (error) {
                        SpeechRecognizer.ERROR_NO_MATCH -> "No speech recognized. Try speaking again."
                        SpeechRecognizer.ERROR_NETWORK -> "Network required for system speech pack, offline speech ready."
                        SpeechRecognizer.ERROR_AUDIO -> "Audio recording error."
                        SpeechRecognizer.ERROR_CLIENT -> "Client recognition error."
                        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission required."
                        else -> "Speech recognition paused."
                    }
                    _errorMessage.value = msg
                }

                override fun onResults(results: Bundle?) {
                    _isListening.value = false
                    _rmsLevel.value = 0f
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val text = matches?.firstOrNull() ?: ""
                    if (text.isNotBlank()) {
                        _recognizedText.value = text
                        onSpeechResultReceived?.invoke(text)
                    }
                }

                override fun onPartialResults(partialResults: Bundle?) {
                    val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val text = matches?.firstOrNull() ?: ""
                    if (text.isNotBlank()) {
                        _recognizedText.value = text
                    }
                }

                override fun onEvent(eventType: Int, params: Bundle?) {}
            })

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                // Prefer offline recognition if available on device
                putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
                if (isChinese) {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, "zh-CN")
                } else {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault().toLanguageTag())
                }
            }

            speechRecognizer?.startListening(intent)
            _isListening.value = true
        } catch (e: Exception) {
            _isListening.value = false
            _errorMessage.value = "SpeechRecognizer init: ${e.localizedMessage}"
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
        } catch (_: Exception) {}
        _isListening.value = false
        _rmsLevel.value = 0f
    }

    fun speak(text: String, isChinese: Boolean = false) {
        if (!isTtsInitialized || text.isBlank()) return
        try {
            if (isChinese) {
                textToSpeech?.language = Locale.CHINESE
            } else {
                textToSpeech?.language = Locale.getDefault()
            }
            textToSpeech?.speak(
                text,
                TextToSpeech.QUEUE_FLUSH,
                null,
                "evo_utterance_${System.currentTimeMillis()}"
            )
            _isSpeaking.value = true
        } catch (_: Exception) {
            _isSpeaking.value = false
        }
    }

    fun stopSpeaking() {
        try {
            textToSpeech?.stop()
        } catch (_: Exception) {}
        _isSpeaking.value = false
    }

    fun simulateVoiceSpeech(simulatedText: String) {
        _recognizedText.value = simulatedText
        onSpeechResultReceived?.invoke(simulatedText)
    }

    fun release() {
        try {
            speechRecognizer?.destroy()
        } catch (_: Exception) {}
        speechRecognizer = null

        try {
            textToSpeech?.stop()
            textToSpeech?.shutdown()
        } catch (_: Exception) {}
        textToSpeech = null
    }
}
