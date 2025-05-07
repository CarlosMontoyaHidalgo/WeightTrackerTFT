package com.aronid.weighttrackertft.data.remote.chatbot

data class ChatbotRequest(val message: String)
data class ChatbotResponse(val response: String)

interface ChatbotApiService {
    suspend fun sendMessage(request: ChatbotRequest): ChatbotResponse
}