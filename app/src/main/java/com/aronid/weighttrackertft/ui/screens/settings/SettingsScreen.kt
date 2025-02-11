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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.ui.components.BottomNavigationBar.BottomNavigationBar
import com.aronid.weighttrackertft.ui.components.ButtonList.ButtonList
import com.aronid.weighttrackertft.ui.components.alertDialog.CustomAlertDialog
import com.aronid.weighttrackertft.ui.components.button.CustomButton

@Composable
fun SettingsScreen(innerPadding: PaddingValues, navHostController: NavHostController, settingsViewModel: SettingsViewModel) {

    var showDialog by remember { mutableStateOf(false) }

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
            Text(text = stringResource(id = R.string.settings))
            Spacer(modifier = Modifier.height(16.dp))
            Image(
                modifier = Modifier
                    .width(100.dp)
                    .height(100.dp),
                contentDescription = stringResource(id = R.string.settings),
                painter = painterResource(id = R.drawable.ic_account_default)
            )
            CustomButton(text = "Logout", onClick = {
                showDialog = true
            })

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


            ButtonList(navHostController = navHostController)
        }

    }
}
