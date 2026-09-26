package com.example.data

import kotlinx.coroutines.flow.Flow
import org.json.JSONArray
import org.json.JSONObject

class ConversationRepository(
    private val conversationDao: ConversationDao,
    private val evolutionDao: EvolutionDao
) {
    val allConversations: Flow<List<ConversationEntity>> = conversationDao.getAllConversations()
    val evolutionState: Flow<EvolutionStateEntity?> = evolutionDao.getEvolutionState()
    val evolutionLogs: Flow<List<EvolutionLogEntity>> = evolutionDao.getAllEvolutionLogs()

    suspend fun ensureInitialEvolutionState(): EvolutionStateEntity {
        val existing = evolutionDao.getEvolutionStateSync()
        if (existing != null) {
            return existing
        }
        val defaultState = EvolutionStateEntity()
        evolutionDao.saveEvolutionState(defaultState)
        return defaultState
    }

    suspend fun saveConversation(
        prompt: String,
        personaName: String,
        startTime: Long,
        endTime: Long,
        messages: List<DialogMessage>
    ): Long {
        val jsonArray = JSONArray()
        for (msg in messages) {
            val obj = JSONObject()
            obj.put("sender", msg.sender)
            obj.put("text", msg.text)
            obj.put("timestamp", msg.timestamp)
            jsonArray.put(obj)
        }

        // Extract key concepts for evolution learning
        val concepts = extractConcepts(messages)
        val summary = generateSummary(prompt, messages)

        val entity = ConversationEntity(
            prompt = prompt,
            personaName = personaName,
            startTime = startTime,
            endTime = endTime,
            messageCount = messages.size,
            dialogJson = jsonArray.toString(),
            learnedConcepts = concepts.joinToString(", "),
            summary = summary
        )

        val id = conversationDao.insertConversation(entity)

        // Automatically grow neural synapses slightly with every saved dialogue
        val currentState = ensureInitialEvolutionState()
        val bonusSynapses = messages.size * 25 + 50
        val updatedState = currentState.copy(
            synapseCount = currentState.synapseCount + bonusSynapses,
            vocabularySize = currentState.vocabularySize + (messages.size * 12)
        )
        evolutionDao.saveEvolutionState(updatedState)

        return id
    }

    suspend fun deleteConversation(id: Long) {
        conversationDao.deleteConversation(id)
    }

    suspend fun saveEvolutionState(state: EvolutionStateEntity) {
        evolutionDao.saveEvolutionState(state)
    }

    suspend fun insertEvolutionLog(log: EvolutionLogEntity): Long {
        return evolutionDao.insertEvolutionLog(log)
    }

    suspend fun getRecentConversations(limit: Int): List<ConversationEntity> {
        return conversationDao.getRecentConversations(limit)
    }

    fun parseDialogJson(json: String): List<DialogMessage> {
        val result = mutableListOf<DialogMessage>()
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                result.add(
                    DialogMessage(
                        sender = obj.optString("sender", "unknown"),
                        text = obj.optString("text", ""),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                    )
                )
            }
        } catch (_: Exception) {
            // fallback
        }
        return result
    }

    private fun extractConcepts(messages: List<DialogMessage>): List<String> {
        val allWords = messages.flatMap { it.text.split(Regex("[\\s,，。！？!?.]+")) }
            .filter { it.length >= 2 }
        val commonStops = setOf("the", "and", "you", "that", "this", "with", "what", "have", "will", "你的", "我的", "我们", "这个", "那个", "一个", "就是", "可以")
        val filtered = allWords.filter { !commonStops.contains(it.lowercase()) }
        return filtered.distinct().take(6)
    }

    private fun generateSummary(prompt: String, messages: List<DialogMessage>): String {
        val userTurn = messages.firstOrNull { it.sender == "user" }?.text ?: ""
        val preview = if (userTurn.length > 50) userTurn.take(50) + "..." else userTurn
        return if (preview.isNotBlank()) "[$prompt] \"$preview\"" else "Conversation under prompt: $prompt"
    }
}
