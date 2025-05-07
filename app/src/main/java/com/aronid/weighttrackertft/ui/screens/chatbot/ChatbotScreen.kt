package com.aronid.weighttrackertft.ui.screens.chatbot

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.navigation.NavigationRoutes
import com.aronid.weighttrackertft.ui.components.chatbot.MessageBubble
import com.aronid.weighttrackertft.ui.components.chatbot.TypingIndicator
import com.aronid.weighttrackertft.ui.components.chatbot.extractExerciseIds
import com.aronid.weighttrackertft.ui.components.chatbot.extractTargetMuscles
import com.aronid.weighttrackertft.ui.screens.routines.createRoutine.CreateRoutineViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatbotScreen(
    innerPadding: PaddingValues,
    viewModel: ChatbotViewModel,
    createRoutineViewModel: CreateRoutineViewModel,
    navHostController: NavHostController
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
                try {
                    if (routineId.isNotBlank()) {
                        //navHostController.navigate("create_routine/$routineId")
                        navHostController.navigate(
                            NavigationRoutes.CreateRoutine.createRoute(
                                routineId
                            )
                        )
                    } else {
                        snackbarHostState.showSnackbar("Error: ID de rutina no válido")
                    }
                } catch (e: Exception) {
                    snackbarHostState.showSnackbar("Error")
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
                // No hacer nada
            }
        }
    }

    // Handle ViewModel snackbar messages
    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearSnackbarMessage()
        }
    }

    // Handle scrolling
    LaunchedEffect(uiState.shouldScrollToBottom) {
        if (uiState.shouldScrollToBottom && uiState.messages.isNotEmpty()) {
            scrollState.animateScrollToItem(uiState.messages.size - 1)
            viewModel.resetScrollFlag()
        }
    }

    // Observar ejercicios y músculos disponibles
    val availableExercises by createRoutineViewModel.availableExercises.collectAsState()
    val availableMuscles by createRoutineViewModel.availableMuscles.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.bot_avatar_no_background),
                            contentDescription = "Avatar del bot",
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.onPrimary),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                        Text(
                            text = "WorkoutBot",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navHostController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { scaffoldPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(scaffoldPadding)
        ) {
            // Message area
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                state = scrollState,
                verticalArrangement = Arrangement.spacedBy(4.dp)
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
                            val targetMuscles = extractTargetMuscles(routineText, availableMuscles)
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

            // Input area
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(
                    value = uiState.userInput,
                    onValueChange = { viewModel.updateUserInput(it) },
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFEBEBEB),
                        unfocusedContainerColor = Color(0xFFEBEBEB),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(24.dp),
                    placeholder = {
                        Text("Escribe un mensaje...", color = Color.Gray)
                    },
                    trailingIcon = {
                        if (uiState.userInput.isNotEmpty()) {
                            IconButton(onClick = { viewModel.clearUserInput() }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Borrar texto"
                                )
                            }
                        }
                    },
                    enabled = !uiState.isLoading
                )

                IconButton(
                    onClick = { viewModel.sendMessage() },
                    enabled = !uiState.isLoading && uiState.userInput.isNotBlank()
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Enviar mensaje",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}