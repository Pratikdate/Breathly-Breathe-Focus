package com.shanacoder.breathly.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PatternDao {
    @Query("SELECT * FROM patterns")
    fun getAllPatterns(): Flow<List<PatternEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPattern(pattern: PatternEntity)

    @Query("DELETE FROM patterns WHERE id = :id")
    suspend fun deletePattern(id: Int)
}
