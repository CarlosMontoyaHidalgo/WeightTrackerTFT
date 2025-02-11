package com.aronid.weighttrackertft.ui.screens.auth.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.navigation.NavigationRoutes
import com.aronid.weighttrackertft.ui.components.authForm.AuthForm

@Composable
fun LoginScreen(
    innerPadding: PaddingValues,
    navHostController: NavHostController,
    ) {
    val viewModel: LoginViewModel = hiltViewModel()

    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_back),
            contentDescription = stringResource(id = R.string.ic_back),
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(vertical = 16.dp)
                .clickable { navHostController.popBackStack() }
        )
        Text(
            text = stringResource(id = R.string.login),
            modifier = Modifier.padding(bottom = 16.dp),
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        AuthForm(
            email = state.email,
            password = state.password,
            onEmailChange = viewModel::onEmailChanged,
            onPasswordChange = viewModel::onPasswordChanged,
            onSubmit = {
                viewModel.loginUser { success ->
                    if (success) {
                        navHostController.navigate(NavigationRoutes.Home.route) {
                            popUpTo(NavigationRoutes.Login.route) { inclusive = true }
                        }
                    }
                }
            },
            isEnabled = state.isFormValid,
            isLoading = state.isLoading,
            emailError = state.emailError?.let { stringResource(it) },
            passwordError = state.passwordError?.let { stringResource(it) },
            formError = state.error?.let { stringResource(it) },
            buttonText = stringResource(R.string.login)
        )

    }
}
