package com.aronid.weighttrackertft.ui.screens.questionnaire.lifeStyle

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.ui.screens.questionnaire.UserQuestionnaireViewModel

@Composable
fun LifeStyleScreen(
    innerPadding: PaddingValues,
    viewModel: UserQuestionnaireViewModel,
    navController: NavHostController
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Estilo de Vida")

        // Aquí puedes agregar preguntas relacionadas con el estilo de vida
        TextField(
            value = viewModel.state.value.lifestyle.activityLevel ?: "",
            onValueChange = { newActivityLevel ->
                viewModel.updateLifestyle(
                    activityLevel = newActivityLevel,
                    goal = viewModel.state.value.lifestyle.goal
                )
            },
            label = { Text("Nivel de Actividad") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(onClick = {
            navController.navigate("physicalData")
        }) {
            Text("Siguiente")
        }
    }
}