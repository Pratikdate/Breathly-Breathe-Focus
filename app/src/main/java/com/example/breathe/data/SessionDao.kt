package com.shanacoder.breathly.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Query("SELECT * FROM sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<SessionEntity>>

    @Insert
    suspend fun insertSession(session: SessionEntity)

    @Query("SELECT SUM(durationSeconds) FROM sessions")
    fun getTotalDuration(): Flow<Int?>

    @Query("SELECT COUNT(id) FROM sessions")
    fun getTotalSessions(): Flow<Int>

    // Get sessions between two timestamps
    @Query("SELECT * FROM sessions WHERE timestamp >= :from AND timestamp < :to ORDER BY timestamp ASC")
    suspend fun getSessionsBetween(from: Long, to: Long): List<SessionEntity>
}
