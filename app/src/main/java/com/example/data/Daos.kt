package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationDao {
    @Query("SELECT * FROM conversations ORDER BY endTime DESC")
    fun getAllConversations(): Flow<List<ConversationEntity>>

    @Query("SELECT COUNT(*) FROM conversations")
    fun getConversationCount(): Flow<Int>

    @Query("SELECT * FROM conversations ORDER BY endTime DESC LIMIT :limit")
    suspend fun getRecentConversations(limit: Int): List<ConversationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: ConversationEntity): Long

    @Query("DELETE FROM conversations WHERE id = :id")
    suspend fun deleteConversation(id: Long)

    @Query("SELECT * FROM conversations WHERE id = :id LIMIT 1")
    suspend fun getConversationById(id: Long): ConversationEntity?
}

@Dao
interface EvolutionDao {
    @Query("SELECT * FROM evolution_state WHERE id = 1 LIMIT 1")
    fun getEvolutionState(): Flow<EvolutionStateEntity?>

    @Query("SELECT * FROM evolution_state WHERE id = 1 LIMIT 1")
    suspend fun getEvolutionStateSync(): EvolutionStateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveEvolutionState(state: EvolutionStateEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvolutionLog(log: EvolutionLogEntity): Long

    @Query("SELECT * FROM evolution_logs ORDER BY timestamp DESC")
    fun getAllEvolutionLogs(): Flow<List<EvolutionLogEntity>>
}
