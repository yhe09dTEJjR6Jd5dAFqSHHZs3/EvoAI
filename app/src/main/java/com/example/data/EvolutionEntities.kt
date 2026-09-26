package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "evolution_state")
data class EvolutionStateEntity(
    @PrimaryKey
    val id: Int = 1,
    val generation: Int = 1,
    val generationTitleEn: String = "Proto-Mind",
    val generationTitleZh: String = "原初思维体",
    val synapseCount: Int = 1200,
    val vocabularySize: Int = 850,
    val empathyLevel: Int = 30, // 0 - 100
    val logicLevel: Int = 35,   // 0 - 100
    val creativityLevel: Int = 30, // 0 - 100
    val unlockedTraits: String = "Curiosity,Basic Reasoning", // Comma-separated
    val evolutionCount: Int = 0,
    val lastEvolutionTime: Long = System.currentTimeMillis()
)

@Entity(tableName = "evolution_logs")
data class EvolutionLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fromGen: Int,
    val toGen: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val insightsGained: String,
    val conversationsProcessed: Int,
    val newTraitUnlocked: String
)
