package com.aronid.weighttrackertft.ui.screens.questionnaire.goals

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.data.questionnaire.LifeStyleOption
import com.aronid.weighttrackertft.ui.components.button.NewCustomButton
import com.aronid.weighttrackertft.ui.components.formScreen.FormScreen
import com.aronid.weighttrackertft.ui.components.cards.CardTextOptionData
import com.aronid.weighttrackertft.ui.components.cards.SelectableCard
import com.aronid.weighttrackertft.utils.button.ButtonType

@Composable
fun GoalsScreen(
    innerPadding: PaddingValues,
    viewModel: GoalsScreenViewModel,
    navHostController: NavHostController
) {
    val state by viewModel.state.collectAsState()
    val buttonConfigs = state.buttonConfigs

    FormScreen(
        modifier = Modifier,
        innerPadding = innerPadding,
        isContentScrolleable = true,
        formContent = {
            Text(
                text = stringResource(id = R.string.life_style),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            GoalsSection(
                options = viewModel.getGoalsOptions(),
                selectedOption = state.goal,
                onOptionSelected = viewModel::onGoalSelected
            )


        },
        formButton = {
            NewCustomButton(
                text = stringResource(id = R.string.next),
                onClick = {
                    viewModel.uploadData(navHostController = navHostController)
                },
                buttonType = ButtonType.FILLED,
                containerColor = Color.Black,
                textConfig = buttonConfigs.textConfig,
                layoutConfig = buttonConfigs.layoutConfig,
                stateConfig = buttonConfigs.stateConfig,
                borderConfig = buttonConfigs.borderConfig
            )
        }

    )
}

@Composable
private fun GoalsSection(
    options: List<LifeStyleOption>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.goals),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        SelectableCard(
            options = options.map {
                CardTextOptionData(
                    id = it.id,
                    text = stringResource(id = it.titleRes),
                    description = stringResource(id = it.descriptionRes)
                )
            },
            selectedOptionId = selectedOption,
            onOptionSelected = onOptionSelected
        )
    }
}