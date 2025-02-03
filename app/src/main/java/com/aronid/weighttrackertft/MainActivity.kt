package com.aronid.weighttrackertft

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.aronid.weighttrackertft.ui.theme.WeightTrackerTFTTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {

    //private lateinit var navHostController: NavHostController
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        enableEdgeToEdge()
        setContent {
            val navHostController = rememberNavController()

            MyApp(navHostController, auth)
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if ( currentUser != null ) {
            //navegar al home
            Log.i("MainActivity", "Usuario logueado")
        } else {
            // No user is signed in
        }
    }
}



@Composable
fun MyApp(navHostController: NavHostController, auth: FirebaseAuth) {
    WeightTrackerTFTTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            NavigationWrapper(innerPadding, navHostController, auth)
        }
    }
}

