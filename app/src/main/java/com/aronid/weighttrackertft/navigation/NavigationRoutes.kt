package com.aronid.weighttrackertft.navigation

sealed class NavigationRoutes(val route: String){
    object Login: NavigationRoutes("logIn")
    object SignUp: NavigationRoutes("signUp")
    object Initial: NavigationRoutes("initial")
    object Home: NavigationRoutes("home")
    object Questionnaire: NavigationRoutes("questionnaire")
    object PhysicalData: NavigationRoutes("physicalData")
    object PersonalInformation: NavigationRoutes("personalInformation")
    object LifeStyle: NavigationRoutes("lifeStyle")
    object Loading: NavigationRoutes("loading")
}