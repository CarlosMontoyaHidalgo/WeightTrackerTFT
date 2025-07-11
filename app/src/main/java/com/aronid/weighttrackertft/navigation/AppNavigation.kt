package com.aronid.weighttrackertft.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.aronid.weighttrackertft.ui.components.calendar.CalendarViewModel
import com.aronid.weighttrackertft.ui.components.dropdown.weightUnitSelector.WeightUnitSelectorViewModel
import com.aronid.weighttrackertft.ui.screens.auth.initial.InitialScreen
import com.aronid.weighttrackertft.ui.screens.auth.login.LoginScreen
import com.aronid.weighttrackertft.ui.screens.auth.signup.SignUpScreen
import com.aronid.weighttrackertft.ui.screens.calendar.CalendarScreen
import com.aronid.weighttrackertft.ui.screens.chatbot.ChatbotScreen
import com.aronid.weighttrackertft.ui.screens.chatbot.ChatbotViewModel
import com.aronid.weighttrackertft.ui.screens.exercises.ExerciseScreen
import com.aronid.weighttrackertft.ui.screens.exercises.ExerciseViewModel
import com.aronid.weighttrackertft.ui.screens.exercises.details.ExerciseDetailsScreen
import com.aronid.weighttrackertft.ui.screens.exercises.details.ExerciseDetailsViewModel
import com.aronid.weighttrackertft.ui.screens.home.HomeScreen
import com.aronid.weighttrackertft.ui.screens.home.HomeViewModel
import com.aronid.weighttrackertft.ui.screens.loading.LoadingScreen
import com.aronid.weighttrackertft.ui.screens.personalization.PersonalizationScreen
import com.aronid.weighttrackertft.ui.screens.progress.charts.ChartsScreen
import com.aronid.weighttrackertft.ui.screens.questionnaire.UserQuestionnaireScreen
import com.aronid.weighttrackertft.ui.screens.questionnaire.UserQuestionnaireViewModel
import com.aronid.weighttrackertft.ui.screens.questionnaire.goals.GoalsScreen
import com.aronid.weighttrackertft.ui.screens.questionnaire.goals.GoalsScreenViewModel
import com.aronid.weighttrackertft.ui.screens.questionnaire.lifeStyle.LifeStyleScreen
import com.aronid.weighttrackertft.ui.screens.questionnaire.lifeStyle.LifeStyleViewModel
import com.aronid.weighttrackertft.ui.screens.questionnaire.personalInformation.PersonalInfoViewModel
import com.aronid.weighttrackertft.ui.screens.questionnaire.personalInformation.PersonalInformationScreen
import com.aronid.weighttrackertft.ui.screens.questionnaire.physicalData.PhysicalDataScreen
import com.aronid.weighttrackertft.ui.screens.questionnaire.physicalData.PhysicalDataViewModel
import com.aronid.weighttrackertft.ui.screens.routines.RoutineScreen
import com.aronid.weighttrackertft.ui.screens.routines.RoutineViewModel
import com.aronid.weighttrackertft.ui.screens.routines.createRoutine.CreateRoutineScreen
import com.aronid.weighttrackertft.ui.screens.routines.createRoutine.CreateRoutineViewModel
import com.aronid.weighttrackertft.ui.screens.routines.details.RoutineDetailsScreen
import com.aronid.weighttrackertft.ui.screens.routines.details.RoutineDetailsViewModel
import com.aronid.weighttrackertft.ui.screens.routines.editRoutines.EditRoutineScreen
import com.aronid.weighttrackertft.ui.screens.routines.editRoutines.EditRoutineViewModel
import com.aronid.weighttrackertft.ui.screens.settings.SettingsScreen
import com.aronid.weighttrackertft.ui.screens.settings.SettingsViewModel
import com.aronid.weighttrackertft.ui.screens.splash.SplashScreen
import com.aronid.weighttrackertft.ui.screens.stats.StatsScreen
import com.aronid.weighttrackertft.ui.screens.user.personalInformation.UserDataScreen
import com.aronid.weighttrackertft.ui.screens.user.personalInformation.UserDataViewModel
import com.aronid.weighttrackertft.ui.screens.workout.WorkoutScreen
import com.aronid.weighttrackertft.ui.screens.workout.WorkoutViewModel
import com.aronid.weighttrackertft.ui.screens.workout.summary.WorkoutSummaryScreen
import com.aronid.weighttrackertft.ui.screens.workout.summary.WorkoutSummaryViewModel
import com.aronid.weighttrackertft.ui.screens.workout.workoutList.WorkoutList


@Composable
fun AppNavigation(
    innerPadding: PaddingValues,
    navHostController: NavHostController,
    isUserLoggedIn: Boolean
) {

    val startDestination =
        if (isUserLoggedIn) NavigationRoutes.Home.route else NavigationRoutes.Loading.route

    NavHost(navHostController, startDestination = startDestination) {
        /*composable(NavigationRoutes.Example.route) {
            ExampleScreen(innerPadding, navHostController)
        }*/

        composable(NavigationRoutes.SplashScreen.route) {
            SplashScreen(innerPadding, isUserLoggedIn, navHostController)
        }

        composable(NavigationRoutes.Loading.route) {
            LoadingScreen(innerPadding, navHostController)
        }

        composable(NavigationRoutes.Initial.route) {
            InitialScreen(
                innerPadding,
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
            val viewModel: HomeViewModel = hiltViewModel()
            HomeScreen(innerPadding, navHostController, viewModel)
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
            val viewModel: PhysicalDataViewModel = hiltViewModel()
            PhysicalDataScreen(
                innerPadding,
                viewModel = viewModel,
                navHostController = navHostController
            )
        }
        composable(NavigationRoutes.PersonalInformation.route) {
            val viewModel: PersonalInfoViewModel = hiltViewModel()
            PersonalInformationScreen(
                innerPadding,
                viewModel = viewModel,
                navHostController = navHostController
            )
        }

        composable(NavigationRoutes.LifeStyle.route) {
            val viewModel: LifeStyleViewModel = hiltViewModel()
            LifeStyleScreen(
                innerPadding,
                viewModel = viewModel,
                navHostController = navHostController
            )
        }
        composable(NavigationRoutes.Goals.route) {
            val viewModel: GoalsScreenViewModel = hiltViewModel()
            GoalsScreen(innerPadding, viewModel = viewModel, navHostController = navHostController)
        }
        composable(NavigationRoutes.Stats.route) {
            StatsScreen(navHostController)
        }

        composable(NavigationRoutes.Settings.route) {
            val viewModel: SettingsViewModel = hiltViewModel()
            SettingsScreen(innerPadding, navHostController, viewModel)
        }

        composable(NavigationRoutes.Exercises.route) {
            val viewModel: ExerciseViewModel = hiltViewModel()
            ExerciseScreen(innerPadding, viewModel, navHostController)
        }

        composable(NavigationRoutes.Routines.route) {
            val viewModel: RoutineViewModel = hiltViewModel()
            RoutineScreen(innerPadding, viewModel, navHostController)
        }

        composable(
            route = "${NavigationRoutes.RoutineDetails.route}?isPredefined={isPredefined}",
            arguments = listOf(
                navArgument("routineId") { type = NavType.StringType },
                navArgument("isPredefined") { type = NavType.BoolType; defaultValue = false }
            )
        ) { backStackEntry ->
            val routineId = backStackEntry.arguments?.getString("routineId")
            val isPredefined = backStackEntry.arguments?.getBoolean("isPredefined") == true
            val viewModel: RoutineDetailsViewModel = hiltViewModel()
            RoutineDetailsScreen(
                innerPadding = innerPadding,
                routineId = routineId,
                isPredefined = isPredefined,
                navHostController = navHostController,
                viewModel = viewModel
            )
        }

        composable(
            NavigationRoutes.EditRoutine.route,
            arguments = listOf(navArgument("routineId") { type = NavType.StringType })
        ) { backStackEntry ->
            val routineId = backStackEntry.arguments?.getString("routineId") ?: ""
            val viewModel: EditRoutineViewModel = hiltViewModel()
            EditRoutineScreen(innerPadding, routineId, navHostController, viewModel)
        }

        composable(NavigationRoutes.ExerciseDetails.route) { backStackEntry ->
            val exerciseId = backStackEntry.arguments?.getString("exerciseId")
            val viewModel: ExerciseDetailsViewModel = hiltViewModel()
            ExerciseDetailsScreen(
                innerPadding,
                exerciseId = exerciseId,
                viewModel,
                navHostController = navHostController
            )
        }

        composable(NavigationRoutes.CreateRoutine.route) {
            val viewModel: CreateRoutineViewModel = hiltViewModel()
            CreateRoutineScreen(innerPadding, viewModel, navHostController)
        }

        composable(
            route = NavigationRoutes.Workout.route,
            arguments = listOf(
                navArgument("routineId") { type = NavType.StringType },
                navArgument("isPredefined") { type = NavType.BoolType; defaultValue = false }
            )
        ) { backStackEntry ->
            val routineId = backStackEntry.arguments?.getString("routineId")
            val isPredefined = backStackEntry.arguments?.getBoolean("isPredefined") ?: false
            val viewModel: WorkoutViewModel = hiltViewModel()
            WorkoutScreen(
                innerPadding = innerPadding,
                routineId = routineId,
                isPredefined = isPredefined,
                navHostController = navHostController,
                viewModel = viewModel
            )
        }

        composable(
            route = NavigationRoutes.WorkoutSummary.route, // "workoutSummary/{workoutId}"
            arguments = listOf(navArgument("workoutId") { type = NavType.StringType })
        ) { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getString("workoutId")
            val viewModel: WorkoutSummaryViewModel = hiltViewModel()
            WorkoutSummaryScreen(
                innerPadding = innerPadding,
                workoutId = workoutId,
                viewModel = viewModel,
                navHostController = navHostController
            )
        }






        composable(NavigationRoutes.WorkoutList.route) {
            WorkoutList(innerPadding, navHostController)
        }

        composable(NavigationRoutes.Personalization.route) {
            PersonalizationScreen(innerPadding, navHostController)
        }

        composable(NavigationRoutes.UserData.route) {
            val viewModel: UserDataViewModel = hiltViewModel()
            val weightUnitViewModel: WeightUnitSelectorViewModel = hiltViewModel()
            UserDataScreen(innerPadding, viewModel, weightUnitViewModel, navHostController)
        }
        composable(NavigationRoutes.Charts.route) {
            ChartsScreen(innerPadding, navHostController)
        }

        /*
        composable(NavigationRoutes.Charts2.route) {
            WeightChartsScreen(innerPadding, navHostController)
        }*/
        composable(NavigationRoutes.Calendar.route) {
            val calendarViewModel: CalendarViewModel = hiltViewModel()
            CalendarScreen(innerPadding, calendarViewModel, navHostController)
        }

        composable(NavigationRoutes.ChatbotScreen.route) {
            val viewModel: ChatbotViewModel = hiltViewModel()
            val createRoutineViewModel: CreateRoutineViewModel = hiltViewModel()
            ChatbotScreen(
                innerPadding,
                viewModel,
                createRoutineViewModel = createRoutineViewModel,
                navHostController
            )
        }



        composable(NavigationRoutes.FavoriteExercises.route) {

        }


//        composable(
//            NavigationRoutes.Training.route,
//            arguments = listOf(navArgument("routineId") { type = NavType.StringType })
//        ) { backStackEntry ->
//            val routineId = backStackEntry.arguments?.getString("routineId")
////            TrainingScreen(innerPadding, routineId = routineId, navHostController)
//
//
//        }

    }
}


