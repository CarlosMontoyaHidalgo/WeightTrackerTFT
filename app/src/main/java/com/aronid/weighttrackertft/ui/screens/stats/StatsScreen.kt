package com.aronid.weighttrackertft.ui.screens.stats


import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.ui.components.navigationBar.BottomNavigationBar.BottomNavigationBar
import com.aronid.weighttrackertft.ui.screens.progress.charts.ChartsScreen

@Composable
fun StatsScreen(
    navHostController: NavHostController,
) {

    Scaffold(
        bottomBar = { BottomNavigationBar(navHostController = navHostController) },
    ) { paddingValues ->
        ChartsScreen(innerPadding = paddingValues, navHostController = navHostController)
    }
}
