package com.aronid.weighttrackertft.ui.screens.settings.SecondaryScreens.GoalsScreen

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
fun GoalsScreen(navHostController: NavHostController) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = stringResource(id = R.string.goals_settings))
        CustomButton(text = stringResource(id = R.string.set_new_goal), onClick = {})
        CustomButton(text = stringResource(id = R.string.view_goals), onClick = {})
        CustomButton(text = stringResource(id = R.string.edit_goals), onClick = {})
    }
}