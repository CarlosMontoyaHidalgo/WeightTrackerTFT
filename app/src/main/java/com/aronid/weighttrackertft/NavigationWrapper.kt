package com.aronid.weighttrackertft

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.aronid.weighttrackertft.ui.screens.auth.initial.InitialScreen
import com.aronid.weighttrackertft.ui.screens.auth.login.LoginScreen
import com.aronid.weighttrackertft.ui.screens.auth.login.LoginViewModel
import com.google.firebase.auth.FirebaseAuth
import com.aronid.weighttrackertft.data.UserRepository
import com.aronid.weighttrackertft.ui.screens.auth.signup.SignUpScreen
//import com.aronid.weighttrackertft.ui.screens.home.HomeScreen

@Composable
fun NavigationWrapper(
    innerPadding: PaddingValues,
    navHostController: NavHostController,
    auth: FirebaseAuth
) {
    val userRepository = UserRepository(auth)
    val loginViewModel = LoginViewModel(userRepository)
    NavHost(navController = navHostController, startDestination = "initial") {
        composable("initial") {
            InitialScreen(
                innerPadding,
                navigateToLogin = { navHostController.navigate("logIn") },
                navigateToSignUp = { navHostController.navigate("signUp") }
            )
        }
        composable("logIn") {
            LoginScreen(innerPadding, navHostController)
        }
        composable("signUp") {
            SignUpScreen(innerPadding, navHostController)
        }
        composable("home") {
            //HomeScreen(innerPadding, navHostController)
        }
    }
}