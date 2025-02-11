package com.aronid.weighttrackertft.ui.screens.settings.SecondaryScreens.EquipmentScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.ui.components.button.CustomButton

@Composable
fun EquipmentScreen(navHostController: NavHostController) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "Equipment Settings")
        CustomButton(text = "Add Equipment", onClick = {})
        CustomButton(text = "Remove Equipment", onClick = {})
        CustomButton(text = "View Equipment List", onClick = {})
    }
}