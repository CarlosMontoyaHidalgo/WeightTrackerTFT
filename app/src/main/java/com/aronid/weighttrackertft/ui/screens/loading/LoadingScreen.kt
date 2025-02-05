package com.aronid.weighttrackertft.ui.screens.loading

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.data.questionnaire.QuestionnaireRepository
import com.aronid.weighttrackertft.navigation.NavigationRoutes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
@Composable
fun LoadingScreen(innerPadding: PaddingValues, navHostController: NavHostController) {

    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val questionnaireRepository = QuestionnaireRepository(db) // Repositorio para obtener el estado

    LaunchedEffect(Unit) {
        val user = auth.currentUser
        if (user != null) {
            try {
                // Usar el repositorio para obtener el estado del cuestionario
                val hasCompletedQuestionnaire = questionnaireRepository.getQuestionnaireStatus(user.uid)

                // Redirigir según si el usuario ha completado el cuestionario
                val destination = if (hasCompletedQuestionnaire) {
                    NavigationRoutes.Home.route
                } else {
                    NavigationRoutes.Questionnaire.route
                }

                navHostController.navigate(destination) {
                    popUpTo(NavigationRoutes.Loading.route) { inclusive = true }
                }
            } catch (e: Exception) {
                // Manejar el error si no se puede acceder a Firestore
                navHostController.navigate(NavigationRoutes.Initial.route) {
                    popUpTo(NavigationRoutes.Loading.route) { inclusive = true }
                }
            }
        } else {
            // Si no hay usuario autenticado, redirigir a la pantalla inicial
            navHostController.navigate(NavigationRoutes.Initial.route) {
                popUpTo(NavigationRoutes.Loading.route) { inclusive = true }
            }
        }
    }

    Box(modifier = Modifier.padding(innerPadding).fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}
