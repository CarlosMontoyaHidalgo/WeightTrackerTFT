package com.aronid.weighttrackertft.ui.screens.workout.workoutList

import android.nfc.tech.MifareUltralight.PAGE_SIZE
import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.aronid.weighttrackertft.data.user.UserRepository
import com.aronid.weighttrackertft.data.workout.WorkoutModel
import com.aronid.weighttrackertft.data.workout.WorkoutRepository
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import java.time.ZoneId
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class WorkoutListViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _dateFilter = MutableStateFlow<Pair<Timestamp?, Timestamp?>>(null to null)
    val dateFilter: StateFlow<Pair<Timestamp?, Timestamp?>> = _dateFilter.asStateFlow()

    val workouts: Flow<PagingData<WorkoutModel>> = dateFilter.flatMapLatest { (startDate, endDate) ->
        Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false, initialLoadSize = 20)
        ) {
            val userId = userRepository.getCurrentUser().uid
            Log.d("WorkoutListViewModel", "Fetching workouts for userId: $userId, startDate: $startDate, endDate: $endDate")
            workoutRepository.getWorkoutsPagingSource(userId, startDate, endDate)
        }.flow
    }.cachedIn(viewModelScope)

    private val _selectedWorkouts = mutableStateMapOf<String, Boolean>()
    val selectedWorkouts: SnapshotStateMap<String, Boolean> = _selectedWorkouts

    fun toggleSelection(workoutId: String, selected: Boolean) {
        if (selected) _selectedWorkouts[workoutId] = true else _selectedWorkouts.remove(workoutId)
        Log.d("WorkoutListViewModel", "Workout $workoutId selected: $selected")
    }

    fun deleteSelectedWorkouts(onResult: (Boolean) -> Unit) {
        val idsToDelete = _selectedWorkouts.keys.toList()
        viewModelScope.launch {
            try {
                workoutRepository.deleteWorkouts(idsToDelete)
                _selectedWorkouts.clear()
                onResult(true) // Éxito
            } catch (e: Exception) {
                Log.e("WorkoutListViewModel", "Error deleting workouts: $idsToDelete", e)
                onResult(false) // Error
            }
        }
    }

    fun setDateFilter(startDate: Timestamp?, endDate: Timestamp?) {
        val adjustedEndDate = endDate?.let {
            val endLocalDate = it.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            val endOfDay = endLocalDate.atTime(23, 59, 59, 999999999)
            Timestamp(Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant()))
        } ?: startDate?.let {
            val startLocalDate = it.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            val endOfDay = startLocalDate.atTime(23, 59, 59, 999999999)
            Timestamp(Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant()))
        }

        _dateFilter.value = startDate to adjustedEndDate
        Log.d("WorkoutListViewModel", "Date filter set: start=$startDate, end=$adjustedEndDate")
    }
    fun clearDateFilter() {
        _dateFilter.value = null to null
        Log.d("WorkoutListViewModel", "Date filter cleared")
    }
}