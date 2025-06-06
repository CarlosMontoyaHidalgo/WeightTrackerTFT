package com.aronid.weighttrackertft.ui.screens.questionnaire.physicalData

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.ui.components.button.NewCustomButton
import com.aronid.weighttrackertft.ui.components.formScreen.FormScreen
import com.aronid.weighttrackertft.utils.button.ButtonType
import com.aronid.weighttrackertft.utils.button.getDefaultBorderConfig
import com.aronid.weighttrackertft.utils.button.getDefaultButtonConfig

@Composable
fun PhysicalDataScreen(
    innerPadding: PaddingValues,
    viewModel: PhysicalDataViewModel,
    navHostController: NavHostController
) {
    val state by viewModel.state.collectAsState()
    val displayedWeight by viewModel.displayedWeight.collectAsState()
    val weightUnit by viewModel.weightUnit.collectAsState()

    var heightError by remember { mutableStateOf(false) }
    var weightError by remember { mutableStateOf(false) }

    FormScreen(
        modifier = Modifier,
        isContentScrolleable = false,
        formContent = {
            Text(
                text = stringResource(id = R.string.physical_data),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.height?.toString() ?: "",
                onValueChange = {
                    viewModel.onHeightChanged(it)
                    heightError = it.isEmpty()
                },
                label = { Text(stringResource(id = R.string.height)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = heightError,
                supportingText = {
                    if (heightError) {
                        Text("R.string.error_required)")
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = displayedWeight,
                    onValueChange = {
                        viewModel.onWeightChanged(it)
                        weightError = it.isEmpty() || it.toDoubleOrNull() == null
                    },
                    label = { Text(stringResource(id = R.string.weight)) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = weightError,
                    supportingText = {
                        when {
                            weightError -> Text(
                                "R.string.error_valid_number",
                                color = MaterialTheme.colorScheme.error
                            )

                            displayedWeight.isEmpty() -> Text(
                                text = stringResource(id = R.string.weight_required)
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.width(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Kilogramos" to "kg", "Libras" to "lbs").forEach { (unit, label) ->
                        Text(
                            text = label,
                            color = if (weightUnit == unit) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier
                                .background(
                                    color = if (weightUnit == unit) MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surface,
                                    shape = MaterialTheme.shapes.small
                                )
                                .clickable { viewModel.setWeightUnit(unit) }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        },
        formButton = {
            val (defaultTextConfig, defaultLayoutConfig, defaultStateConfig) = getDefaultButtonConfig(
                state.height != null && state.weight != null
            )
            val defaultBorderConfig = getDefaultBorderConfig()

            NewCustomButton(
                text = stringResource(id = R.string.submit),
                onClick = { viewModel.uploadData(navHostController) },
                buttonType = ButtonType.FILLED,
                containerColor = MaterialTheme.colorScheme.primary,
                textConfig = defaultTextConfig,
                layoutConfig = defaultLayoutConfig,
                stateConfig = defaultStateConfig,
                borderConfig = defaultBorderConfig
            )
        },
        innerPadding = innerPadding
    )
}