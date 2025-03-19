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
    object Workout : NavigationRoutes("workout/{routineId}?isPredefined={isPredefined}") {
        fun createRoute(routineId: String, isPredefined: Boolean = false): String {
            return "workout/$routineId?isPredefined=$isPredefined"
        }
    }
    object WorkoutSummary : NavigationRoutes("workoutSummary/{workoutId}"){
        fun createRoute(workoutId: String) = "workoutSummary/$workoutId"
    }

    object WorkoutList : NavigationRoutes("workoutList")

    object Routines : NavigationRoutes("routines")


    object RoutineDetails : NavigationRoutes("routine_details/{routineId}"){
        fun createRoute(routineId: String) = "routine_details/$routineId"
    }
    object EditRoutine : NavigationRoutes("edit_routine/{routineId}"){
        fun createRoute(routineId: String) = "edit_routine/$routineId"
    }
    object Training: NavigationRoutes("training/{routineId}"){
        fun createRoute(routineId: String) = "training/$routineId"
    }

    object CreateRoutine : NavigationRoutes("createRoutine")
    object ChooseExercises : NavigationRoutes("chooseExercises")
    object Personalization : NavigationRoutes("personalization")

    object ExerciseDetails : NavigationRoutes("exerciseDetails/{exerciseId}"){
        fun createRoute(exerciseId: String) = "exerciseDetails/$exerciseId"
    }

    object UserData : NavigationRoutes("userData")



}