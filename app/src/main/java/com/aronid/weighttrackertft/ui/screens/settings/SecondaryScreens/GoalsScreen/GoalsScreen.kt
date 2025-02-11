package com.aronid.weighttrackertft.ui.screens.settings.SecondaryScreens.GoalsScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.ui.components.button.CustomButton

@Composable
fun GoalsScreen(navHostController: NavHostController) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "Goals Settings")
        CustomButton(text = "Set New Goal", onClick = {})
        CustomButton(text = "View Goals", onClick = {})
        CustomButton(text = "Edit Goals", onClick = {})
    }
}