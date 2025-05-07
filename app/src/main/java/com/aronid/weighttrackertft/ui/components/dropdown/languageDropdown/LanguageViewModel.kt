package com.aronid.weighttrackertft.ui.components.dropdown.languageDropdown

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aronid.weighttrackertft.data.room.language.LanguageConfig
import com.aronid.weighttrackertft.data.room.language.LanguageConfigDao
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LanguageViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun saveLanguage(language: String) {
        viewModelScope.launch {
            sharedPreferences.edit().putString("selected_language", language).apply()
        }
    }

    suspend fun getLanguage(): String {
        return sharedPreferences.getString("selected_language", "English") ?: "English"
    }
}