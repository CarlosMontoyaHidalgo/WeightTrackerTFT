package com.aronid.weighttrackertft.data.room.language


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LanguageConfigDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(config: LanguageConfig)

    @Query("SELECT * FROM language_config WHERE id = 1")
    suspend fun getConfig(): LanguageConfig?
}