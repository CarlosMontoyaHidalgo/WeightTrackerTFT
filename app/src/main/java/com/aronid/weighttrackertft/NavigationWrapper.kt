package com.aronid.weighttrackertft

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.aronid.weighttrackertft.presentation.initial.InitialScreen
import com.aronid.weighttrackertft.presentation.login.LoginScreen
import com.aronid.weighttrackertft.presentation.signup.SignUpScreen
import com.google.firebase.auth.FirebaseAuth

//https://www.youtube.com/watch?v=LxABxtwhrDE&ab_channel=Programaci%C3%B3nAndroidbyAristiDevs

@Composable
fun NavigationWrapper(
    innerPadding: PaddingValues,
    navHostController: NavHostController,
    auth: FirebaseAuth
) {
    NavHost(navController = navHostController, startDestination = "initial") {
        composable("initial") {
            InitialScreen(
                innerPadding,
                navigateToLogin = { navHostController.navigate("logIn") },
                navigateToSignUp = { navHostController.navigate("signUp") }
            )
        }
        composable("logIn") {
            LoginScreen(innerPadding, auth, navHostController)
        }
        composable("signUp") {
            SignUpScreen(innerPadding, auth, navHostController)
        }
    }
}