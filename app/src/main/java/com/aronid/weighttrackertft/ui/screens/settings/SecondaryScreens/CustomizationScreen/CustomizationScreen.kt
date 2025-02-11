package com.aronid.weighttrackertft.ui.screens.settings.SecondaryScreens.CustomizationScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.ui.components.button.CustomButton

@Composable
fun CustomizationScreen(navHostController: NavHostController) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "Customization Settings")
        CustomButton(text = "Change Theme", onClick = {})
        CustomButton(text = "Change Language", onClick = {})
        CustomButton(text = "Change Font Size", onClick = {})
    }
}