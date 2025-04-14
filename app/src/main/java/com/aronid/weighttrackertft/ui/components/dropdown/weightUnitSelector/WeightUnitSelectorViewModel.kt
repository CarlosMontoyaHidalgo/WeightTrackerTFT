package com.aronid.weighttrackertft.ui.components.dropdown.weightUnitSelector

import androidx.lifecycle.ViewModel
import com.aronid.weighttrackertft.data.weight.WeightRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WeightUnitSelectorViewModel @Inject constructor(
    private val weightUnitRepository: WeightRepository
) : ViewModel() {
    val weightUnit = weightUnitRepository.weightUnit

    fun setWeightUnit(unit: String) {
        weightUnitRepository.setWeightUnit(unit)
    }
}