package com.aronid.weighttrackertft.di

import com.aronid.weighttrackertft.data.exercises.ExerciseRepository
import com.aronid.weighttrackertft.data.questionnaire.QuestionnaireRepository
import com.aronid.weighttrackertft.data.routine.RoutineCustomRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideQuestionnaireRepository(
        firestore: FirebaseFirestore
    ): QuestionnaireRepository {
        return QuestionnaireRepository(firestore)
    }

    @Provides
    @Singleton
    fun provideRoutineRepository(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth
    ): RoutineCustomRepository {
        return RoutineCustomRepository(firestore, auth)
    }

    @Provides
    @Singleton
    fun provideExerciseRepository(firestore: FirebaseFirestore): ExerciseRepository {
        return ExerciseRepository(firestore)
    }

//    @Provides
//    @Singleton
//    fun provideExerciseRepository(
//        firestore: FirebaseFirestore
//    ): ExerciseRepository {
//        return ExerciseRepository(firestore)
//    }
}
