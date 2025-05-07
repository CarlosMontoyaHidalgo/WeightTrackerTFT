package com.aronid.weighttrackertft.data.goals

data class GoalOption(
    val id: String,
    val title: String,
    val description: String
)

fun getGoalOptions(): List<GoalOption> {
    return listOf(
        GoalOption("lose_weight", "Perder peso", "Perder peso para mejorar la salud"),
        GoalOption("maintain", "Mantener peso", "Mantener el peso actual"),
        GoalOption("gain_muscle", "Ganar músculo", "Aumentar la masa muscular")
    )
}