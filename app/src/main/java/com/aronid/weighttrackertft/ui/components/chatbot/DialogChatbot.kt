package com.aronid.weighttrackertft.ui.components.chatbot

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.navigation.NavigationRoutes
import com.aronid.weighttrackertft.ui.screens.chatbot.ChatbotViewModel
import com.aronid.weighttrackertft.ui.screens.routines.createRoutine.CreateRoutineViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatbotDialog(
    viewModel: ChatbotViewModel,
    createRoutineViewModel: CreateRoutineViewModel,
    navHostController: NavHostController,
    onDismiss: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberLazyListState()

    val createRoutineState by createRoutineViewModel.state.collectAsState()
    LaunchedEffect(createRoutineState) {
        when (createRoutineState) {
            is CreateRoutineViewModel.State.Success -> {
                val routineId =
                    (createRoutineState as CreateRoutineViewModel.State.Success).routineId
                snackbarHostState.showSnackbar("Rutina guardada.")
                if (routineId.isNotBlank()) {
                    navHostController.navigate(NavigationRoutes.CreateRoutine.createRoute(routineId))
                    onDismiss()
                } else {
                    snackbarHostState.showSnackbar("Error: ID de rutina no válido")
                }
            }

            is CreateRoutineViewModel.State.Error -> {
                val errorMessage =
                    (createRoutineState as CreateRoutineViewModel.State.Error).message
                snackbarHostState.showSnackbar("Error al guardar: $errorMessage")
            }

            is CreateRoutineViewModel.State.Loading -> {
                snackbarHostState.showSnackbar("Guardando rutina...")
            }

            is CreateRoutineViewModel.State.Initial -> {
                // No action
            }
        }
    }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearSnackbarMessage()
        }
    }

    LaunchedEffect(uiState.shouldScrollToBottom) {
        if (uiState.shouldScrollToBottom && uiState.messages.isNotEmpty()) {
            scrollState.animateScrollToItem(uiState.messages.size - 1)
            viewModel.resetScrollFlag()
        }
    }

    val availableExercises by createRoutineViewModel.availableExercises.collectAsState()
    val availableMuscles by createRoutineViewModel.availableMuscles.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.bot_avatar_no_background),
                        contentDescription = "Avatar del bot",
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onPrimary),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                    Text(
                        text = "WorkoutBot",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 450.dp)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp), // Reduced from 16.dp
                    state = scrollState,
                    verticalArrangement = Arrangement.spacedBy(4.dp) // Reduced from 8.dp
                ) {
                    items(uiState.messages) { message ->
                        MessageBubble(
                            message = message,
                            availableExercises = availableExercises,
                            availableMuscles = availableMuscles,
                            onSaveRoutine = { routineText ->
                                val name = "Rutina del Chatbot - ${
                                    SimpleDateFormat(
                                        "dd/MM/yyyy",
                                        Locale.getDefault()
                                    ).format(Date())
                                }"
                                val description = routineText
                                val exerciseIds = extractExerciseIds(routineText)
                                val targetMuscles =
                                    extractTargetMuscles(routineText, availableMuscles)
                                createRoutineViewModel.createRoutine(
                                    name = name,
                                    goal = "Entrenamiento general",
                                    description = description,
                                    exerciseIds = exerciseIds,
                                    targetMuscles = targetMuscles
                                )
                            }
                        )
                    }
                    item {
                        if (uiState.isLoading) {
                            TypingIndicator()
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextField(
                        value = uiState.userInput,
                        onValueChange = { viewModel.updateUserInput(it) },
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 56.dp)
                            .clip(RoundedCornerShape(32.dp)),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF5F5F5),
                            unfocusedContainerColor = Color(0xFFF5F5F5),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            cursorColor = MaterialTheme.colorScheme.primary
                        ),
                        textStyle = MaterialTheme.typography.bodyLarge,
                        placeholder = {
                            Text(
                                "Pregunta sobre ejercicios...",
                                color = Color.Gray.copy(alpha = 0.6f),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
                        trailingIcon = {
                            if (uiState.userInput.isNotEmpty()) {
                                IconButton(onClick = { viewModel.clearUserInput() }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Borrar texto",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        },
                        enabled = !uiState.isLoading
                    )

                    IconButton(
                        onClick = { viewModel.sendMessage() },
                        enabled = !uiState.isLoading && uiState.userInput.isNotBlank(),
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Enviar mensaje",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.padding(end = 4.dp) // Reduced from 8.dp
            ) {
                Text(
                    text = "Cerrar",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth(fraction = 0.98f)
            .padding(8.dp)
            .shadow(8.dp, shape = RoundedCornerShape(16.dp)),
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp)
    )

    SnackbarHost(
        hostState = snackbarHostState,
        modifier = Modifier.padding(16.dp)
    )
}