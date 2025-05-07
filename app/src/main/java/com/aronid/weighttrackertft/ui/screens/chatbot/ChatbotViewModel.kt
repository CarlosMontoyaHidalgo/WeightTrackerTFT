package com.aronid.weighttrackertft.ui.screens.chatbot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aronid.weighttrackertft.data.chatbot.ChatbotRepository
import com.aronid.weighttrackertft.ui.components.chatbot.ChatMessage
import com.aronid.weighttrackertft.ui.components.chatbot.isRoutineMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatbotViewModel @Inject constructor(
    private val repository: ChatbotRepository
) : ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow(ChatbotUiState())
    val uiState: StateFlow<ChatbotUiState> = _uiState.asStateFlow()

    init {
        sendInitialMessage()
    }

    fun updateUserInput(input: String) {
        _uiState.value = _uiState.value.copy(userInput = input)
    }

    fun clearUserInput() {
        _uiState.value = _uiState.value.copy(userInput = "")
    }

    fun sendMessage() {
        val message = _uiState.value.userInput.trim()
        if (message.isBlank()) return

        // Add user message
        val userMessage = ChatMessage(message, isUser = true)
        addMessage(userMessage)

        // Set loading state
        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            val result = repository.sendMessage(message)
            val botResponse = result.fold(
                onSuccess = { it.response },
                onFailure = { "Error: ${it.message ?: "No se pudo conectar al servidor"}" }
            )

            if (botResponse.startsWith("Error")) {
                _uiState.value = _uiState.value.copy(
                    snackbarMessage = botResponse,
                    isLoading = false
                )
            } else {
                val isRoutine = isRoutineMessage(botResponse)
                val botMessage = ChatMessage(botResponse, isUser = false, isRoutine = isRoutine)
                addMessage(botMessage)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    userInput = "",
                    shouldScrollToBottom = true
                )
            }
        }
    }

    private fun sendInitialMessage() {
        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            val result = repository.sendInitialMessage()
            val botResponse = result.fold(
                onSuccess = { it.response },
                onFailure = { "Error: ${it.message ?: "No se pudo conectar al servidor"}" }
            )

            if (botResponse.startsWith("Error")) {
                _uiState.value = _uiState.value.copy(
                    snackbarMessage = botResponse,
                    isLoading = false
                )
            } else {
                val botMessage = ChatMessage(botResponse, isUser = false, isRoutine = false)
                addMessage(botMessage)
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    private fun addMessage(message: ChatMessage) {
        val updatedMessages = _uiState.value.messages + message
        _uiState.value = _uiState.value.copy(messages = updatedMessages)
    }

    fun clearSnackbarMessage() {
        _uiState.value = _uiState.value.copy(snackbarMessage = null)
    }

    fun resetScrollFlag() {
        _uiState.value = _uiState.value.copy(shouldScrollToBottom = false)
    }
}

data class ChatbotUiState(
    val messages: List<ChatMessage> = emptyList(),
    val userInput: String = "",
    val isLoading: Boolean = false,
    val snackbarMessage: String? = null,
    val shouldScrollToBottom: Boolean = false
)