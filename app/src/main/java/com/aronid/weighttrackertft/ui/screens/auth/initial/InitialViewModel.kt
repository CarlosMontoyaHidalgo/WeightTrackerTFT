package com.aronid.weighttrackertft.ui.screens.auth.initial

import android.content.Context
import androidx.lifecycle.ViewModel
import java.util.Locale

class InitialViewModel : ViewModel() {

    private var _currentLanguage = Locale.getDefault().displayLanguage
    val currentLanguage: String get() = _currentLanguage

    fun changeLanguage(context: Context, selectedLanguage: String) {
        val languageCode = when (selectedLanguage) {
            "English" -> "en"
            "Spanish" -> "es"
            else -> "en"
        }

        setAppLocale(context, languageCode)
        _currentLanguage = selectedLanguage
    }

    private fun setAppLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources = context.resources
        val configuration = resources.configuration
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }
}
