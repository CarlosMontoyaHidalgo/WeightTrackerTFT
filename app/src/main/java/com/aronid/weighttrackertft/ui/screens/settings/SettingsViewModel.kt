package com.aronid.weighttrackertft.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aronid.weighttrackertft.data.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {
    fun logout(){
        viewModelScope.launch{
            try {
                userRepository.logout()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}