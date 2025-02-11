package com.aronid.weighttrackertft.ui.screens.loading

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
    val questionnaireRepository = QuestionnaireRepository(db)

    LaunchedEffect(Unit) {
        val user = auth.currentUser
        if (user != null) {
            try {
                val hasCompletedQuestionnaire = questionnaireRepository.getQuestionnaireStatus(user.uid)

                val destination = if (hasCompletedQuestionnaire) {
                    NavigationRoutes.Home.route
                } else {
                    NavigationRoutes.Questionnaire.route
                }

                navHostController.navigate(destination) {
                    popUpTo(NavigationRoutes.Loading.route) { inclusive = true }
                }
            } catch (e: Exception) {
                navHostController.navigate(NavigationRoutes.Initial.route) {
                    popUpTo(NavigationRoutes.Loading.route) { inclusive = true }
                }
            }
        } else {
            navHostController.navigate(NavigationRoutes.Initial.route) {
                popUpTo(NavigationRoutes.Loading.route) { inclusive = true }
            }
        }
    }

    Box(modifier = Modifier.padding(innerPadding).fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimaryContainer)
    }
}
