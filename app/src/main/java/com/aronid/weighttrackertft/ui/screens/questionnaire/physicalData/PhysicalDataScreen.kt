package com.aronid.weighttrackertft.ui.screens.questionnaire.physicalData

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.ui.screens.questionnaire.UserQuestionnaireViewModel

@Composable
fun PhysicalDataScreen(
    viewModel: UserQuestionnaireViewModel,
    navController: NavHostController
) {
    // Recolectamos el estado de la vista con collectAsState()
    val physicalData = viewModel.state.collectAsState().value.physicalData

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Datos Físicos")

        // Campo para la altura
        TextField(
            value = physicalData.height?.toString() ?: "", // Usamos el valor de altura
            onValueChange = { newHeight ->
                viewModel.updatePhysicalData(height = newHeight.toIntOrNull() ?: 0, weight = physicalData.weight ?: 0.0)
            },
            label = { Text("Altura (cm)") },
            modifier = Modifier.fillMaxWidth()
        )

        // Campo para el peso
        TextField(
            value = physicalData.weight?.toString() ?: "", // Usamos el valor de peso
            onValueChange = { newWeight ->
                viewModel.updatePhysicalData(height = physicalData.height ?: 0, weight = newWeight.toDoubleOrNull() ?: 0.0)
            },
            label = { Text("Peso (kg)") },
            modifier = Modifier.fillMaxWidth()
        )

        // Botón para finalizar el cuestionario
        Button(onClick = {
            viewModel.submitQuestionnaire()  // Guardar los datos al finalizar
            navController.navigate("home") // Redirigir a la pantalla principal
        }) {
            Text("Finalizar")
        }
    }
}
