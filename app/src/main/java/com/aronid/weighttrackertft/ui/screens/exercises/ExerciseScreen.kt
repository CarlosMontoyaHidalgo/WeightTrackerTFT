package com.aronid.weighttrackertft.ui.screens.exercises

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.ui.components.BottomNavigationBar.BottomNavigationBar
import com.aronid.weighttrackertft.ui.components.searchBar.MySearchBar

@Composable
fun ExerciseScreen(
    innerPadding: PaddingValues,
    navHostController: NavHostController
) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navHostController = navHostController) },
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(paddingValues)) {
            Text(text = "Bienvenido a Stats")
            MySearchBar()
        }

    }
}
