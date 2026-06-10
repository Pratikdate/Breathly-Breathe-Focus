package com.shanacoder.breathly.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "patterns")
data class PatternEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val inhale: Float,
    val hold1: Float,
    val exhale: Float,
    val hold2: Float,
    val cycles: Int,
    val colorHex: Long,
    val isFavorite: Boolean = false,
    val description: String = "",
    val benefits: String = "",
    val methods: String = ""
)
