package com.aronid.weighttrackertft.ui.screens.routines.editRoutines

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.ui.components.exercise.exerciseCard.ExerciseCard

@Composable
fun EditRoutineScreen(
    innerPadding: PaddingValues,
    routineId: String,
    navHostController: NavHostController,
    viewModel: EditRoutineViewModel
) {
    val routine by viewModel.routine.collectAsState()
    val exercises by viewModel.exercises.collectAsState()

    // Estados para los campos editables
    var name by remember { mutableStateOf(routine?.name ?: "") }
    var goal by remember { mutableStateOf(routine?.goal ?: "") }
    var description by remember { mutableStateOf(routine?.description ?: "") }

    // Cargar la rutina al iniciar
    LaunchedEffect(routineId) {
        viewModel.loadRoutine(routineId)
    }

    Column(
        modifier = Modifier
            .padding(innerPadding)
            .padding(16.dp)
    ) {
        Text(
            text = "Editar Rutina",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Campos editables
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = goal,
            onValueChange = { goal = it },
            label = { Text("Objetivo") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Ejercicios",
            style = MaterialTheme.typography.headlineSmall
        )

        LazyColumn {
            items(exercises) { exercise ->
                ExerciseCard(
                    name = exercise.name,
                    primaryMuscle = exercise.primaryMuscle?.id ?: "",
                    secondaryMuscles = exercise.secondaryMuscle.map { it.id },
                    imageUrl = R.drawable.background,
                    isSelected = true,
                    onCardClick = { viewModel.removeExercise(exercise.id) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para añadir ejercicios
        Button(
            onClick = {
                navHostController.navigate("choose_exercises")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Añadir Ejercicios")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para guardar cambios
        val context = LocalContext.current
        Button(
            onClick = {
                if (name.isNotBlank()) {
                    viewModel.updateRoutine(
                        name = name,
                        goal = goal,
                        description = description,
                        exerciseIds = exercises.map { it.id }
                    )
                    Toast.makeText(context, "Rutina actualizada correctamente", Toast.LENGTH_SHORT)
                        .show()
                    navHostController.popBackStack()
                } else {
                    Toast.makeText(context, "El nombre no puede estar vacío", Toast.LENGTH_SHORT)
                        .show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar Cambios")
        }
    }
}