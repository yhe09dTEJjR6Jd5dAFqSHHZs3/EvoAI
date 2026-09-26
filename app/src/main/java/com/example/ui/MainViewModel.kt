package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.ConversationEntity
import com.example.data.ConversationRepository
import com.example.data.DialogMessage
import com.example.data.EvolutionLogEntity
import com.example.data.EvolutionStateEntity
import com.example.engine.EvolutionEngine
import com.example.engine.OfflineAiBrain
import com.example.engine.OfflineVoiceManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale

enum class AppScreen {
    HOME,
    VOICE_CONVERSATION,
    EVOLUTION_CEREMONY
}

enum class AiVoiceState {
    IDLE,
    LISTENING,
    THINKING,
    SPEAKING
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ConversationRepository
    val voiceManager: OfflineVoiceManager
    private val aiBrain = OfflineAiBrain()

    val allConversations: StateFlow<List<ConversationEntity>>
    val evolutionState: StateFlow<EvolutionStateEntity?>
    val evolutionLogs: StateFlow<List<EvolutionLogEntity>>

    // App Navigation
    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Language setting: true = Chinese, false = English
    private val _isChinese = MutableStateFlow(
        Locale.getDefault().language.startsWith("zh")
    )
    val isChinese: StateFlow<Boolean> = _isChinese.asStateFlow()

    // Prompt input
    private val _promptText = MutableStateFlow("哲学家（苏格拉底式思辨）")
    val promptText: StateFlow<String> = _promptText.asStateFlow()

    // Live Conversation State
    private val _currentDialogMessages = MutableStateFlow<List<DialogMessage>>(emptyList())
    val currentDialogMessages: StateFlow<List<DialogMessage>> = _currentDialogMessages.asStateFlow()

    private val _aiVoiceState = MutableStateFlow(AiVoiceState.IDLE)
    val aiVoiceState: StateFlow<AiVoiceState> = _aiVoiceState.asStateFlow()

    private val _liveSpokenText = MutableStateFlow("")
    val liveSpokenText: StateFlow<String> = _liveSpokenText.asStateFlow()

    private val _showEndConfirmDialog = MutableStateFlow(false)
    val showEndConfirmDialog: StateFlow<Boolean> = _showEndConfirmDialog.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    // Selected conversation for history detail inspection
    private val _selectedConversation = MutableStateFlow<ConversationEntity?>(null)
    val selectedConversation: StateFlow<ConversationEntity?> = _selectedConversation.asStateFlow()

    // Evolution Ceremony State
    private val _evolutionStep = MutableStateFlow(0) // 0=thinking, 1=synapses, 2=traits, 3=complete
    val evolutionStep: StateFlow<Int> = _evolutionStep.asStateFlow()

    private val _latestEvolutionResult = MutableStateFlow<EvolutionEngine.EvolutionResult?>(null)
    val latestEvolutionResult: StateFlow<EvolutionEngine.EvolutionResult?> = _latestEvolutionResult.asStateFlow()

    private var conversationStartTime: Long = 0L
    private var listeningJob: Job? = null

    init {
        val db = AppDatabase.getDatabase(application)
        repository = ConversationRepository(db.conversationDao(), db.evolutionDao())
        voiceManager = OfflineVoiceManager(application)

        allConversations = repository.allConversations.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        evolutionState = repository.evolutionState.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

        evolutionLogs = repository.evolutionLogs.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        viewModelScope.launch {
            repository.ensureInitialEvolutionState()
        }

        // Configure voice manager callbacks
        voiceManager.onSpeechResultReceived = { recognized ->
            onUserVoiceCaptured(recognized)
        }

        // Keep initial prompt aligned with default language
        if (!_isChinese.value) {
            _promptText.value = "Philosophical Sage (Socratic inquiry)"
        }
    }

    fun setLanguage(chinese: Boolean) {
        _isChinese.value = chinese
        if (chinese && _promptText.value == "Philosophical Sage (Socratic inquiry)") {
            _promptText.value = "哲学家（苏格拉底式思辨）"
        } else if (!chinese && _promptText.value == "哲学家（苏格拉底式思辨）") {
            _promptText.value = "Philosophical Sage (Socratic inquiry)"
        }
    }

    fun updatePrompt(newPrompt: String) {
        _promptText.value = newPrompt
    }

    /**
     * Step 2 in CUJ:
     * 点击“开始”按钮 → 开始和AI对话（用嘴）
     */
    fun startVoiceConversation() {
        val prompt = _promptText.value.ifBlank {
            if (_isChinese.value) "全能智慧伙伴" else "Intelligent Companion"
        }

        conversationStartTime = System.currentTimeMillis()
        _currentDialogMessages.value = emptyList()
        _liveSpokenText.value = ""
        _showEndConfirmDialog.value = false
        _currentScreen.value = AppScreen.VOICE_CONVERSATION

        // Initial greeting
        viewModelScope.launch {
            _aiVoiceState.value = AiVoiceState.THINKING
            val initialGreeting = if (_isChinese.value) {
                "你好！基于提示词“$prompt”，我的神经语音链路已就绪。请用嘴对我说任何你想交流的话题！"
            } else {
                "Hello! Tuned to '$prompt', our voice link is active. Speak whatever is on your mind!"
            }

            delay(400)
            val aiMsg = DialogMessage(sender = "ai", text = initialGreeting)
            _currentDialogMessages.value = listOf(aiMsg)

            // Speak greeting
            _aiVoiceState.value = AiVoiceState.SPEAKING
            voiceManager.speak(initialGreeting, _isChinese.value)

            // Wait for TTS to finish before auto-listening
            startPeriodicTtsWatch()
        }
    }

    private fun startPeriodicTtsWatch() {
        listeningJob?.cancel()
        listeningJob = viewModelScope.launch {
            // Wait while TTS is speaking
            while (voiceManager.isSpeaking.value) {
                delay(200)
            }
            delay(350)
            if (_currentScreen.value == AppScreen.VOICE_CONVERSATION && !_showEndConfirmDialog.value) {
                _aiVoiceState.value = AiVoiceState.LISTENING
                voiceManager.startListening(_isChinese.value)
            }
        }
    }

    /**
     * Handled when user voice input is transcribed
     */
    fun onUserVoiceCaptured(spokenText: String) {
        if (spokenText.isBlank()) return
        voiceManager.stopListening()

        val userMsg = DialogMessage(sender = "user", text = spokenText)
        val updatedHistory = _currentDialogMessages.value + userMsg
        _currentDialogMessages.value = updatedHistory
        _liveSpokenText.value = ""

        // AI processes and answers
        viewModelScope.launch {
            _aiVoiceState.value = AiVoiceState.THINKING
            val currentState = evolutionState.value ?: repository.ensureInitialEvolutionState()

            val aiAnswer = aiBrain.generateResponse(
                prompt = _promptText.value,
                userSpeech = spokenText,
                history = updatedHistory,
                evolutionState = currentState
            )

            val aiMsg = DialogMessage(sender = "ai", text = aiAnswer)
            _currentDialogMessages.value = updatedHistory + aiMsg

            _aiVoiceState.value = AiVoiceState.SPEAKING
            voiceManager.speak(aiAnswer, _isChinese.value)

            startPeriodicTtsWatch()
        }
    }

    fun triggerMicToggle() {
        if (voiceManager.isListening.value) {
            voiceManager.stopListening()
            _aiVoiceState.value = AiVoiceState.IDLE
        } else {
            voiceManager.stopSpeaking()
            _aiVoiceState.value = AiVoiceState.LISTENING
            voiceManager.startListening(_isChinese.value)
        }
    }

    /**
     * Step 3 & 4 in CUJ:
     * 点击“结束”按钮 → 弹出再次确认对话框
     */
    fun onEndButtonClicked() {
        _showEndConfirmDialog.value = true
        voiceManager.stopSpeaking()
        voiceManager.stopListening()
        _aiVoiceState.value = AiVoiceState.IDLE
    }

    fun dismissEndConfirmDialog() {
        _showEndConfirmDialog.value = false
        // Resume listening if still on voice screen
        _aiVoiceState.value = AiVoiceState.LISTENING
        voiceManager.startListening(_isChinese.value)
    }

    /**
     * Step 5 & 6 in CUJ:
     * 再次确认 → 对话结束 → 自动保存对话内容
     */
    fun confirmEndAndSaveConversation() {
        _showEndConfirmDialog.value = false
        voiceManager.stopSpeaking()
        voiceManager.stopListening()
        _aiVoiceState.value = AiVoiceState.IDLE

        val messagesToSave = _currentDialogMessages.value
        val promptUsed = _promptText.value
        val endTime = System.currentTimeMillis()

        viewModelScope.launch {
            if (messagesToSave.isNotEmpty()) {
                repository.saveConversation(
                    prompt = promptUsed,
                    personaName = promptUsed.take(24),
                    startTime = conversationStartTime,
                    endTime = endTime,
                    messages = messagesToSave
                )
                _toastMessage.value = if (_isChinese.value) {
                    "对话已结束，并自动保存至本地记忆库！"
                } else {
                    "Conversation ended & automatically saved to neural memory!"
                }
            }
            _currentScreen.value = AppScreen.HOME
        }
    }

    /**
     * Step 7 in CUJ:
     * 点击“进化”按钮 → AI开始进化
     */
    fun startEvolution() {
        val savedList = allConversations.value
        val currentState = evolutionState.value ?: EvolutionStateEntity()
        _currentScreen.value = AppScreen.EVOLUTION_CEREMONY
        _evolutionStep.value = 0

        viewModelScope.launch {
            // Cinematic evolution stages
            delay(900)
            _evolutionStep.value = 1 // Synthesizing memories

            delay(1100)
            _evolutionStep.value = 2 // Restructuring neural weights

            val result = EvolutionEngine.executeEvolution(currentState, savedList)
            _latestEvolutionResult.value = result

            // Commit to Room Database
            repository.saveEvolutionState(result.updatedState)
            repository.insertEvolutionLog(result.log)

            delay(1000)
            _evolutionStep.value = 3 // Evolution complete!
        }
    }

    fun completeEvolution() {
        _currentScreen.value = AppScreen.HOME
        _evolutionStep.value = 0
        _toastMessage.value = if (_isChinese.value) {
            "AI 进化完成！已跃迁至新一代认知系统！"
        } else {
            "AI Evolution complete! Cognition upgraded to next generation!"
        }
    }

    fun selectConversationForDetail(conv: ConversationEntity) {
        _selectedConversation.value = conv
    }

    fun dismissConversationDetail() {
        _selectedConversation.value = null
    }

    fun deleteConversation(id: Long) {
        viewModelScope.launch {
            repository.deleteConversation(id)
            if (_selectedConversation.value?.id == id) {
                _selectedConversation.value = null
            }
        }
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun navigateToHome() {
        voiceManager.stopSpeaking()
        voiceManager.stopListening()
        _currentScreen.value = AppScreen.HOME
    }

    override fun onCleared() {
        super.onCleared()
        voiceManager.release()
    }
}
