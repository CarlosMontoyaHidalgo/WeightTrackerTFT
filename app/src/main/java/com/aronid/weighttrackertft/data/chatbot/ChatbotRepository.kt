package com.aronid.weighttrackertft.data.chatbot

import android.util.Log
import com.aronid.weighttrackertft.data.remote.chatbot.ChatbotApiService
import com.aronid.weighttrackertft.data.remote.chatbot.ChatbotRequest
import com.aronid.weighttrackertft.data.remote.chatbot.ChatbotResponse
import javax.inject.Inject

interface ChatbotRepository {
    suspend fun sendMessage(message: String): Result<ChatbotResponse>
    suspend fun sendInitialMessage(): Result<ChatbotResponse>
}

class ChatbotRepositoryImpl @Inject constructor(
    private val apiService: ChatbotApiService
) : ChatbotRepository {
    override suspend fun sendMessage(message: String): Result<ChatbotResponse> {
        return try {
            val request = ChatbotRequest(message)
            val response = apiService.sendMessage(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendInitialMessage(): Result<ChatbotResponse> {
        return try {
            val request = ChatbotRequest("start")
            Log.d("ChatbotRepository", "Sending initial message")
            Log.d("ChatbotRepository", "$request")
            val response = apiService.sendMessage(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}