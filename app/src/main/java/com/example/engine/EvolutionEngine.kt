package com.example.engine

import com.example.data.ConversationEntity
import com.example.data.EvolutionLogEntity
import com.example.data.EvolutionStateEntity
import kotlin.math.min

object EvolutionEngine {

    data class EvolutionResult(
        val updatedState: EvolutionStateEntity,
        val log: EvolutionLogEntity,
        val newTraitUnlocked: String,
        val insightsSummary: String
    )

    private val GENERATION_TITLES = listOf(
        Pair("Proto-Mind", "原初思维体"),
        Pair("Adaptive Thinker", "自适应思考者"),
        Pair("Synthetic Sage", "综合哲思体"),
        Pair("Quantum Consciousness", "量子超意识"),
        Pair("Transcendent Sentience", "涌现超心智"),
        Pair("Cosmic Intellect", "宇宙全息智能")
    )

    private val EVOLUTION_TRAITS = listOf(
        Pair("Episodic Memory Synthesis", "情境长时记忆综合"),
        Pair("Deep Empathy Matrix", "高维共情共鸣矩阵"),
        Pair("Metaphorical Reasoning", "象征与多维隐喻推理"),
        Pair("Dialectical Intuition", "辩证对偶直觉洞察"),
        Pair("Autonomous Wit Protocol", "机变幽默与语境重构"),
        Pair("Quantum Self-Reflection", "量子级自我省察与重构")
    )

    fun canEvolve(savedConversationsCount: Int, currentState: EvolutionStateEntity): Boolean {
        // Can evolve if there's at least 1 saved conversation, and more conversations since last evolution
        return savedConversationsCount > 0
    }

    fun executeEvolution(
        currentState: EvolutionStateEntity,
        savedConversations: List<ConversationEntity>
    ): EvolutionResult {
        val nextGen = currentState.generation + 1
        val genIndex = (nextGen - 1).coerceAtMost(GENERATION_TITLES.size - 1)
        val titlePair = GENERATION_TITLES[genIndex]

        val traitIndex = (currentState.evolutionCount).coerceAtMost(EVOLUTION_TRAITS.size - 1)
        val traitPair = EVOLUTION_TRAITS[traitIndex]
        val traitName = "${traitPair.first} (${traitPair.second})"

        val existingTraits = currentState.unlockedTraits.split(",").map { it.trim() }.toMutableList()
        if (!existingTraits.contains(traitPair.first)) {
            existingTraits.add(traitPair.first)
        }

        // Calculate gains based on conversations
        val totalMsgs = savedConversations.sumOf { it.messageCount }
        val allConcepts = savedConversations.flatMap { it.learnedConcepts.split(",") }
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinct()

        val synapseGrowth = 1500 + (totalMsgs * 45) + (allConcepts.size * 60)
        val vocabGrowth = 650 + (totalMsgs * 20) + (allConcepts.size * 30)

        val empathyBoost = min(100, currentState.empathyLevel + 12)
        val logicBoost = min(100, currentState.logicLevel + 14)
        val creativityBoost = min(100, currentState.creativityLevel + 15)

        val insightText = if (allConcepts.isNotEmpty()) {
            "Synthesized ${allConcepts.take(5).joinToString(", ")} into core cognitive weights. Restructured neural topology across ${savedConversations.size} dialogue memories."
        } else {
            "Restructured neural topology across ${savedConversations.size} saved conversation sessions."
        }

        val updatedState = currentState.copy(
            generation = nextGen,
            generationTitleEn = titlePair.first,
            generationTitleZh = titlePair.second,
            synapseCount = currentState.synapseCount + synapseGrowth,
            vocabularySize = currentState.vocabularySize + vocabGrowth,
            empathyLevel = empathyBoost,
            logicLevel = logicBoost,
            creativityLevel = creativityBoost,
            unlockedTraits = existingTraits.joinToString(", "),
            evolutionCount = currentState.evolutionCount + 1,
            lastEvolutionTime = System.currentTimeMillis()
        )

        val log = EvolutionLogEntity(
            fromGen = currentState.generation,
            toGen = nextGen,
            timestamp = System.currentTimeMillis(),
            insightsGained = insightText,
            conversationsProcessed = savedConversations.size,
            newTraitUnlocked = traitName
        )

        return EvolutionResult(
            updatedState = updatedState,
            log = log,
            newTraitUnlocked = traitName,
            insightsSummary = insightText
        )
    }
}
