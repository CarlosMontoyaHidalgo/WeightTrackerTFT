package com.aronid.weighttrackertft.ui.components.fields.email

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class EmailFieldViewModel @Inject constructor(): ViewModel(){

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _showError = MutableStateFlow(false)
    val showError: StateFlow<Boolean> = _showError

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail.trim()
    }

    fun onShowError() {
        _showError.value = true
    }

    fun validateEmail(email: String): String?{
        return if(_showError.value){
            when {
                email.isEmpty() -> "El correo electrónico no puede estar vacío"
                !email.contains("@") -> "Recuerda el @"
                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Correo electrónico no válido"
                else -> null
            }
        } else { null }
    }

    fun isEmailValid(): Boolean{
        return validateEmail(_email.value) == null
    }

    fun clearEmail(){
        _email.value = ""
    }


}