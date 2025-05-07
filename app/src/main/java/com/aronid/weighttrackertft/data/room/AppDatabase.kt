package com.aronid.weighttrackertft.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aronid.weighttrackertft.data.room.language.LanguageConfig
import com.aronid.weighttrackertft.data.room.language.LanguageConfigDao

@Database(entities = [LanguageConfig::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun languageConfigDao(): LanguageConfigDao
}