package com.aronid.weighttrackertft

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.aronid.weighttrackertft.navigation.AppNavigation
import com.aronid.weighttrackertft.ui.theme.WeightTrackerTFTTheme
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //auth = Firebase.auth
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val savedLanguage = sharedPreferences.getString("selected_language", "English") ?: "English"
        Log.d("MainActivity", "Saved language: $savedLanguage")
        updateLocale(savedLanguage)

        enableEdgeToEdge()
        setContent {
            MyApp(auth)
        }
    }

    private fun updateLocale(language: String) {
        val locale = when (language) {
            "English" -> Locale("en")
            "Spanish" -> Locale("es")
            else -> Locale.getDefault()
        }
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}


@Composable
fun MyApp(auth: FirebaseAuth) {
    val navHostController = rememberNavController()
    var isUserLoggedIn by remember { mutableStateOf(auth.currentUser != null) }

    LaunchedEffect(auth) {
        auth.addAuthStateListener {
            isUserLoggedIn = it.currentUser != null
        }
    }

    WeightTrackerTFTTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            AppNavigation(innerPadding, navHostController, isUserLoggedIn)
        }
    }
}

