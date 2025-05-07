package com.aronid.weighttrackertft.ui.screens.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.navigation.NavigationRoutes
import com.aronid.weighttrackertft.ui.components.alertDialog.CustomAlertDialog
import com.aronid.weighttrackertft.ui.components.button.MyElevatedButton
import com.aronid.weighttrackertft.ui.components.navigationBar.BottomNavigationBar.BottomNavigationBar

@Composable
fun SettingsScreen(
    innerPadding: PaddingValues,
    navHostController: NavHostController,
    settingsViewModel: SettingsViewModel
) {

    var showDialog by remember { mutableStateOf(false) }
    val state by settingsViewModel.buttonState.collectAsState()
    val buttonConfigs = state.baseState.buttonConfigs

    LaunchedEffect(Unit) {
        settingsViewModel.updateButtonConfigs()
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(navHostController = navHostController) },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier
                    .width(100.dp)
                    .height(100.dp),
                contentDescription = stringResource(id = R.string.settings),
                painter = painterResource(id = R.drawable.ic_account_default)
            )
            Spacer(Modifier.height(24.dp))
            MyElevatedButton(
                onClick = { navHostController.navigate(NavigationRoutes.UserData.route) },
                text = stringResource(id = R.string.personal_data),
                modifier = Modifier,
                height = 50.dp
            )

            Spacer(Modifier.height(16.dp))
            MyElevatedButton(
                onClick = { navHostController.navigate(NavigationRoutes.Personalization.route) },
                text = stringResource(id = R.string.customization),
                modifier = Modifier,
                height = 50.dp
            )

            Spacer(Modifier.height(16.dp))
            MyElevatedButton(
                onClick = { navHostController.navigate(NavigationRoutes.WorkoutList.route) },
                text = stringResource(id = R.string.workout_history),
                modifier = Modifier,
                height = 50.dp
            )

            Spacer(Modifier.height(16.dp))
            MyElevatedButton(
                onClick = {
                    showDialog = true
                },
                text = stringResource(id = R.string.logout),
                modifier = Modifier,
                buttonColor = Color.Red,
                textColor = Color.White,
                height = 50.dp
            )

            CustomAlertDialog(
                showDialog = showDialog,
                onDismiss = { showDialog = false },
                onConfirm = {
                    settingsViewModel.logout()
                    navHostController.navigate("initial")
                },
                title = stringResource(id = R.string.alert_title),
                text = stringResource(id = R.string.alert_message),
                confirmButtonText = stringResource(id = R.string.alert_positive),
                dismissButtonText = stringResource(id = R.string.alert_negative)
            )

        }

    }
}
