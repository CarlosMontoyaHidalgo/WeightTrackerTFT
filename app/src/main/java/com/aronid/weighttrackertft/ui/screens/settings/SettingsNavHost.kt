package com.aronid.weighttrackertft.ui.screens.settings

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.aronid.weighttrackertft.navigation.NavigationRoutes

@Composable
fun SettingsNavHost(navHostController: NavHostController){
    NavHost(navHostController, startDestination = NavigationRoutes.Settings.route) {
        composable(NavigationRoutes.UserSettings.route){

        }
    }
}