package com.aronid.weighttrackertft.domain.usecase

import com.aronid.weighttrackertft.data.user.UserModel
import com.aronid.weighttrackertft.data.user.UserRepository

class SaveUserUseCase(private val userRepository: UserRepository) {
    suspend fun execute(userModel: UserModel) {
        userRepository.saveUser(userModel)
    }
}