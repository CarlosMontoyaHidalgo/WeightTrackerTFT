package com.aronid.weighttrackertft.ui.screens.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import com.aronid.weighttrackertft.ui.components.button.BackButton
import com.aronid.weighttrackertft.ui.components.button.NewCustomButton
import com.aronid.weighttrackertft.ui.components.navigationBar.BottomNavigationBar.BottomNavigationBar
import com.aronid.weighttrackertft.utils.button.ButtonType

@Composable
fun SettingsScreen(
    innerPadding: PaddingValues,
    navHostController: NavHostController,
    settingsViewModel: SettingsViewModel
) {

    var showDialog by remember { mutableStateOf(false) }
    val state by settingsViewModel.buttonState.collectAsState()
    val buttonConfigs = state.baseState.buttonConfigs
//    val name by settingsViewModel.userName.collectAsState()


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
//            Text(name)

            NewCustomButton(
                text = stringResource(id = R.string.personal_data),
                onClick = {
                    navHostController.navigate(NavigationRoutes.UserData.route)
                },
                buttonType = ButtonType.FILLED,
                containerColor = Color.Black,
                textConfig = buttonConfigs.textConfig,
                layoutConfig = buttonConfigs.layoutConfig,
                stateConfig = buttonConfigs.stateConfig,
                borderConfig = buttonConfigs.borderConfig
            )

            NewCustomButton(
                text = stringResource(id = R.string.customization),
                onClick = {
                    navHostController.navigate(NavigationRoutes.Personalization.route)
                },
                buttonType = ButtonType.FILLED,
                containerColor = Color.Black,
                textConfig = buttonConfigs.textConfig,
                layoutConfig = buttonConfigs.layoutConfig,
                stateConfig = buttonConfigs.stateConfig,
                borderConfig = buttonConfigs.borderConfig
            )

            NewCustomButton(
                text = "History Workout",
                onClick = {
                    navHostController.navigate(NavigationRoutes.WorkoutList.route)
                },
                buttonType = ButtonType.FILLED,
                containerColor = Color.Black,
                textConfig = buttonConfigs.textConfig,
                layoutConfig = buttonConfigs.layoutConfig,
                stateConfig = buttonConfigs.stateConfig,
                borderConfig = buttonConfigs.borderConfig
            )

            NewCustomButton(
                text = stringResource(id = R.string.logout),
                onClick = {
                    showDialog = true
                },
                buttonType = ButtonType.FILLED,
                containerColor = Color.Red,
                textConfig = buttonConfigs.textConfig,
                layoutConfig = buttonConfigs.layoutConfig,
                stateConfig = buttonConfigs.stateConfig,
                borderConfig = buttonConfigs.borderConfig
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

//            ButtonList(navHostController = navHostController)

        }

    }
}
