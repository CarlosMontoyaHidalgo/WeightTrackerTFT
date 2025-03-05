package com.aronid.weighttrackertft.ui.screens.progress
//
//import androidx.lifecycle.ViewModel
//import com.aronid.weighttrackertft.data.user.UserRepository
//import com.aronid.weighttrackertft.data.workout.WorkoutRepository
//import dagger.hilt.android.lifecycle.HiltViewModel
//import javax.inject.Inject
//
//@HiltViewModel
//class ProgressViewModel @Inject constructor(
//    private val workoutRepo: WorkoutRepository,
//    private val userRepo: UserRepository
//) : ViewModel() {

//    fun calculateWeeklyProgress(): Map<String, Any> {
//        return mapOf(
//            "caloriesBurned" to calculateTotalCalories(),
//            "weightChange" to calculateWeightChange(),
//            "volumeProgress" to calculateVolumeTrend()
//        )
//    }
//
//    private suspend fun calculateTotalCalories(): Double {
//        val workouts = workoutRepo.getLastWeekWorkouts()
//        return workouts.sumOf { it.calories ?: 0.0 }
//    }
//
//    private suspend fun calculateWeightChange(): Double {
//        val currentWeight = userRepo.getCurrentUserWeight() ?: 0.0
//        val initialWeight = userRepo.getInitialWeight()
//        return currentWeight - initialWeight
//    }
//}