package com.aronid.weighttrackertft.ui.components.dropdown.weightReminder

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class WeightReminderViewModel @Inject constructor() : ViewModel() {

    private val _reminderInterval = MutableStateFlow(7)
    val reminderInterval: StateFlow<Int> = _reminderInterval.asStateFlow()

    fun setReminderInterval(days: Int) {
        _reminderInterval.value = days.coerceAtLeast(1)
    }
}