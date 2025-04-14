package com.aronid.weighttrackertft.ui.screens.settings.SecondaryScreens.UserSettingsScreen

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
fun UserSettingsScreen(navHostController: NavHostController){
    Column(modifier = Modifier.fillMaxSize()){
        Text(text = stringResource(id = R.string.user_settings))
        CustomButton(text = stringResource(id = R.string.change_personal_information), onClick = {})
        CustomButton(text = stringResource(id = R.string.change_password), onClick = {})
        CustomButton(text = stringResource(id = R.string.notification_settings), onClick = {})
    }
}