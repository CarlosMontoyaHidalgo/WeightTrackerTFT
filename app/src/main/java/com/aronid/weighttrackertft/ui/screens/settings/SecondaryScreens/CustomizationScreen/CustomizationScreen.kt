package com.aronid.weighttrackertft.ui.screens.settings.SecondaryScreens.CustomizationScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.ui.components.button.CustomButton

@Composable
fun CustomizationScreen(navHostController: NavHostController) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = stringResource(id = R.string.customization_settings))
        CustomButton(text = stringResource(id = R.string.change_theme), onClick = {})
        CustomButton(text = stringResource(id = R.string.change_language), onClick = {})
        CustomButton(text = stringResource(id = R.string.change_font_size), onClick = {})
    }
}