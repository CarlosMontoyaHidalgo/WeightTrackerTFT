package com.aronid.weighttrackertft.ui.screens.questionnaire.physicalData

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.ui.components.button.CustomButton
import com.aronid.weighttrackertft.ui.screens.questionnaire.UserQuestionnaireViewModel

@Composable
fun PhysicalDataScreen(
    viewModel: UserQuestionnaireViewModel,
    navController: NavHostController
) {
    val physicalData = viewModel.state.collectAsState().value.physicalData

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            stringResource(id = R.string.physical_data),
            color = MaterialTheme.colorScheme.onBackground
        )

        TextField(
            value = physicalData.height?.toString() ?: "",
            onValueChange = { newHeight ->
                viewModel.updatePhysicalData(
                    height = newHeight.toIntOrNull() ?: 0,
                    weight = physicalData.weight ?: 0.0
                )
            },
            label = { Text(stringResource(id = R.string.height)) },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = physicalData.weight?.toString() ?: "",
            onValueChange = { newWeight ->
                viewModel.updatePhysicalData(
                    height = physicalData.height ?: 0,
                    weight = newWeight.toDoubleOrNull() ?: 0.0
                )
            },
            label = { Text(stringResource(id = R.string.weight)) },
            modifier = Modifier.fillMaxWidth()
        )

        CustomButton(
            text = stringResource(id = R.string.submit),
            textColor = MaterialTheme.colorScheme.onPrimary,
            containerColor = MaterialTheme.colorScheme.onBackground,
            onClick = {
                viewModel.submitQuestionnaire()
                navController.navigate("home")
            })
    }
}
