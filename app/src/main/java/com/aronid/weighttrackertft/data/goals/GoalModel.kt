package com.aronid.weighttrackertft.data.goals

data class GoalOption(
    val id: String,
    val title: String,
    val description: String
)

fun getGoalOptions(): List<GoalOption> {
    return listOf(
        GoalOption("lose_weight", "Lose Weight", "Lose weight to improve health"),
        GoalOption("maintain", "Maintain", "Maintain current weight"),
        GoalOption("gain_muscle", "Gain Muscle", "Gain muscle mass")
    )
}