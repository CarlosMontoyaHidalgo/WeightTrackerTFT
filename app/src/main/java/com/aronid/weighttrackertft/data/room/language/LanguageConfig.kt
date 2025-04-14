package com.aronid.weighttrackertft.data.room.language

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "language_config")
data class LanguageConfig(
    @PrimaryKey val id: Int = 1,
    val languageCode: String
)