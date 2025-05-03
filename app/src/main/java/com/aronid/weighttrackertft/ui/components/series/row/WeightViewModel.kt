package com.aronid.weighttrackertft.ui.components.series.row

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aronid.weighttrackertft.data.weight.UserWeightRepository
import com.aronid.weighttrackertft.data.weight.WeightEntryModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeightViewModel @Inject constructor(
    private val weightRepo: UserWeightRepository
) : ViewModel() {
    private val _weightEntries = MutableStateFlow<List<WeightEntryModel>>(emptyList())
    val weightEntries: StateFlow<List<WeightEntryModel>> = _weightEntries.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            try {
                _weightEntries.value = weightRepo.getWeightHistory()
            } catch (e: Exception) {
                Log.e("WeightVM", "Error cargando datos", e)
                // Manejar error (mostrar mensaje al usuario)
            }
        }
    }

    fun saveWeight(weight: Double, note: String = "") {
        viewModelScope.launch {
            try {
                weightRepo.saveWeightEntry(weight, note)
                // Actualizar la lista después de guardar
                _weightEntries.value = weightRepo.getWeightHistory()
            } catch (e: Exception) {
                Log.e("WeightVM", "Error guardando peso", e)
                // Manejar error (mostrar mensaje al usuario)
            }
        }
    }
}