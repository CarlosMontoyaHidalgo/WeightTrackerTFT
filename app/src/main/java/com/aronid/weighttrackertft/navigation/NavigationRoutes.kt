package com.aronid.weighttrackertft.navigation

sealed class NavigationRoutes(val route: String) {
    object Login : NavigationRoutes("logIn")
    object SignUp : NavigationRoutes("signUp")
    object Initial : NavigationRoutes("initial")
    object Home : NavigationRoutes("home")
    object Questionnaire : NavigationRoutes("questionnaire")
    object PhysicalData : NavigationRoutes("physicalData")
    object PersonalInformation : NavigationRoutes("personalInformation")
    object LifeStyle : NavigationRoutes("lifeStyle")
    object Goals : NavigationRoutes("goals")
    object Loading : NavigationRoutes("loading")
    object Stats : NavigationRoutes("stats")
    object Exercises : NavigationRoutes("Exercises")

    /*Settings*/
    object Settings : NavigationRoutes("settings")
    object UserSettings : NavigationRoutes("userSettings")
    object Customization : NavigationRoutes("customization")
    object Equipment : NavigationRoutes("equipment")

    /*Workouts*/
    object Routines : NavigationRoutes("routines")
    object RoutineDetails: NavigationRoutes("routine_details/{routineId}"){
        fun createRoute(routineId: String) = "routine_details/$routineId"
    }
    object CreateRoutine : NavigationRoutes("createRoutine")
    object ChooseExercises : NavigationRoutes("chooseExercises")


}