package com.aronid.weighttrackertft.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.aronid.weighttrackertft.ui.screens.auth.initial.InitialScreen
import com.aronid.weighttrackertft.ui.screens.auth.login.LoginScreen
import com.aronid.weighttrackertft.ui.screens.auth.signup.SignUpScreen
import com.aronid.weighttrackertft.ui.screens.home.HomeScreen
import com.aronid.weighttrackertft.ui.screens.loading.LoadingScreen
import com.aronid.weighttrackertft.ui.screens.questionnaire.UserQuestionnaireScreen
import com.aronid.weighttrackertft.ui.screens.questionnaire.UserQuestionnaireViewModel
import com.aronid.weighttrackertft.ui.screens.questionnaire.lifeStyle.LifeStyleScreen
import com.aronid.weighttrackertft.ui.screens.questionnaire.personalInformation.PersonalInformationScreen
import com.aronid.weighttrackertft.ui.screens.questionnaire.physicalData.PhysicalDataScreen
import com.aronid.weighttrackertft.ui.screens.settings.SettingsScreen
import com.aronid.weighttrackertft.ui.screens.settings.SettingsViewModel
import com.aronid.weighttrackertft.ui.screens.stats.StatsScreen
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


@Composable
fun AppNavigation(
    innerPadding: PaddingValues,
    navHostController: NavHostController,
    isUserLoggedIn: Boolean
) {

    val startDestination =
        if (isUserLoggedIn) NavigationRoutes.Home.route else NavigationRoutes.Loading.route

    NavHost(navHostController, startDestination = startDestination) {

        composable(NavigationRoutes.Loading.route) {
            LoadingScreen(innerPadding, navHostController)
        }

        composable(NavigationRoutes.Initial.route) {
            InitialScreen(innerPadding,
                navigateToLogin = { navHostController.navigate(NavigationRoutes.Login.route) },
                navigateToSignUp = { navHostController.navigate(NavigationRoutes.SignUp.route) }
            )
        }


        composable(NavigationRoutes.Login.route) {
            LoginScreen(innerPadding, navHostController)
        }
        composable(NavigationRoutes.SignUp.route) {
            SignUpScreen(innerPadding, navHostController)
        }
        composable(NavigationRoutes.Home.route) {
            HomeScreen(innerPadding, navHostController, db = Firebase.firestore)
        }

        composable(NavigationRoutes.Questionnaire.route) {
            val viewModel: UserQuestionnaireViewModel = hiltViewModel()
            UserQuestionnaireScreen(
                innerPadding,
                viewModel = viewModel,
                navHostController = navHostController
            )
        }
        composable(NavigationRoutes.PhysicalData.route) {
            val viewModel: UserQuestionnaireViewModel = hiltViewModel()
            PhysicalDataScreen(viewModel = viewModel, navController = navHostController)
        }
        composable(NavigationRoutes.PersonalInformation.route) {
            val viewModel: UserQuestionnaireViewModel = hiltViewModel()
            PersonalInformationScreen(
                innerPadding,
                viewModel = viewModel,
                navController = navHostController
            )
        }

        composable(NavigationRoutes.LifeStyle.route) {
            val viewModel: UserQuestionnaireViewModel = hiltViewModel()
            LifeStyleScreen(innerPadding, viewModel = viewModel, navController = navHostController)
        }
        composable(NavigationRoutes.Stats.route) {
            StatsScreen(innerPadding, navHostController)
        }

        composable(NavigationRoutes.Settings.route) {
            val viewModel: SettingsViewModel = hiltViewModel()
            SettingsScreen(innerPadding, navHostController, viewModel)
        }



    }
}


