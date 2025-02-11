package com.aronid.weighttrackertft.ui.screens.questionnaire.lifeStyle

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.ui.components.button.CustomButton
import com.aronid.weighttrackertft.ui.screens.questionnaire.UserQuestionnaireViewModel

@Composable
fun LifeStyleScreen(
    innerPadding: PaddingValues,
    viewModel: UserQuestionnaireViewModel,
    navController: NavHostController
) {
    Column(modifier = Modifier.padding(innerPadding)) {
        Text(stringResource(id = R.string.life_style), color = MaterialTheme.colorScheme.onPrimary)

        TextField(
            value = viewModel.state.value.lifestyle.activityLevel ?: "",
            onValueChange = { newActivityLevel ->
                viewModel.updateLifestyle(
                    activityLevel = newActivityLevel,
                    goal = viewModel.state.value.lifestyle.goal
                )
            },
            label = { Text(stringResource(id = R.string.activity_level)) },
            modifier = Modifier.fillMaxWidth(),
        )

        CustomButton(
            text = stringResource(id = R.string.next),
            textColor = MaterialTheme.colorScheme.onPrimary,
            onClick = {
                navController.navigate("physicalData")
            })
    }
}