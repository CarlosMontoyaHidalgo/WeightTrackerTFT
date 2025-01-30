package com.aronid.weighttrackertft

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.aronid.weighttrackertft.ui.theme.WeightTrackerTFTTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeightTrackerTFTTheme {
                MyApp()
            }
        }
    }
}


@Composable
fun MyApp(){
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        HomeScreen(innerPadding)
    }
}

@Composable
fun HomeScreen(innerPadding: PaddingValues) {
    Column(Modifier.padding(innerPadding)){

    }
}
