package com.aronid.weighttrackertft.data.weight

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeightRepository @Inject constructor() {
    private val _weightUnit = MutableStateFlow("Kilogramos")
    val weightUnit: StateFlow<String> = _weightUnit.asStateFlow()

    fun setWeightUnit(unit: String) {
        _weightUnit.value = unit
    }
}