package com.aronid.weighttrackertft.ui.screens.settings.SecondaryScreens.UserSettingsScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.ui.components.button.CustomButton

@Composable
fun UserSettingsScreen(navHostController: NavHostController){
    Column(modifier = Modifier.fillMaxSize()){
        Text(text = "User Settings")
        CustomButton(text = "Change Personal Information", onClick = {})
        CustomButton(text = "Change Password", onClick = {})
        CustomButton(text = "Notification Settings", onClick = {})
    }
}