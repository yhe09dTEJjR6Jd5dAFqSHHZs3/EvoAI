package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val prompt: String,
    val personaName: String,
    val startTime: Long,
    val endTime: Long,
    val messageCount: Int,
    val dialogJson: String, // Serialized list of DialogMessage
    val learnedConcepts: String, // Comma-separated insights/keywords
    val summary: String
)

data class DialogMessage(
    val sender: String, // "user" or "ai"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)
