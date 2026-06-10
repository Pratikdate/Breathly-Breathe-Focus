package com.shanacoder.breathly.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val patternName: String,
    val cycles: Int,
    val durationSeconds: Int,
    val timestamp: Long
)
