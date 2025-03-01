package com.aronid.weighttrackertft.ui.screens.workout

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.aronid.weighttrackertft.ui.components.formScreen.FormScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Mock ViewModel for Preview
class MockWorkoutViewModel : ViewModel() {
    private val _calories = MutableStateFlow<Int?>(null)
    val calories: StateFlow<Int?> = _calories.asStateFlow()

    private val _volume = MutableStateFlow<Int?>(null)
    val volume: StateFlow<Int?> = _volume.asStateFlow()

    fun loadCalories() {
        viewModelScope.launch {
            // Simulate loading data
            _calories.value = 500 // Example calorie value
            _volume.value = 1000 // Example volume value
        }
    }
}

@HiltViewModel
class WorkoutViewModel1 @Inject constructor(
    // ... your dependencies ...
) : ViewModel() {
    private val _calories = MutableStateFlow<Int?>(null)
    val calories: StateFlow<Int?> = _calories.asStateFlow()

    private val _volume = MutableStateFlow<Int?>(null)
    val volume: StateFlow<Int?> = _volume.asStateFlow()

    fun loadCalories() {
        viewModelScope.launch {
            // Simulate loading data
            _calories.value = 500 // Example calorie value
            _volume.value = 1000 // Example volume value
        }
    }
}

@Composable
fun WorkoutSummaryScreen(
    innerPadding: PaddingValues,
    viewModel: WorkoutViewModel1,
    navHostController: NavHostController
) {
    val calories by viewModel.calories.collectAsState()
    val volume by viewModel.volume.collectAsState()

    val currentViewModel by rememberUpdatedState(newValue = viewModel)

    LaunchedEffect(Unit) {
        currentViewModel.loadCalories()
    }


    FormScreen(
        modifier = Modifier,
        innerPadding = innerPadding,
        isContentScrolleable = false,
        formContent = {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (calories != null) {
                    Text(
                        text = "Calorías quemadas: $calories kcal",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp), elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (volume != null) {
                    Text(
                        text = "Volumen del entrenamiento: $volume kg",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }


        },
        formButton = {}
    )
}


@Preview(showBackground = true)
@Composable
fun WorkoutSummaryScreenPreview() {
    val navController = rememberNavController()
    val viewModel: WorkoutViewModel1 = remember { WorkoutViewModel1() }
    WorkoutSummaryScreen(
        innerPadding = PaddingValues(16.dp),
        viewModel = viewModel,
        navHostController = navController
    )
}