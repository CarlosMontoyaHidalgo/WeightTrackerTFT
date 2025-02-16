package com.aronid.weighttrackertft.ui.screens.questionnaire.physicalData

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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

    FormScreen(
        modifier = Modifier,
        innerPadding = innerPadding,
        isContentScrolleable = false,
        formContent = {
            Text(
                stringResource(id = R.string.physical_data),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.padding(16.dp))

            TextField(
                value = state.height?.toString() ?: "",
                onValueChange = viewModel::onHeightChanged,
                label = { Text(stringResource(id = R.string.height)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                maxLines = 1,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                ),
                isError = state.height == null,
                supportingText = {
                    if (state.height == null) {
                        Text("Este campo es requerido")
                    }
                }

            )

            OutlinedTextField(
                value = state.weight?.toString() ?: "",
                onValueChange = viewModel::onWeightChanged,
                label = { Text(stringResource(id = R.string.weight)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = state.weight == null,
                supportingText = {
                    if (state.weight == null) {
                        Text("Este campo es requerido")
                    }
                }

            )

        },
        formButton = {
            val (defaultTextConfig, defaultLayoutConfig, defaultStateConfig) = getDefaultButtonConfig(
                state.height != null && state.weight != null
            )
            val defaultBorderConfig = getDefaultBorderConfig()

            NewCustomButton(
                text = stringResource(id = R.string.submit),
                onClick = {
                    viewModel.uploadData(navHostController)
                },
                buttonType = ButtonType.FILLED,
                containerColor = MaterialTheme.colorScheme.onBackground,
                textConfig = defaultTextConfig,
                layoutConfig = defaultLayoutConfig,
                stateConfig = defaultStateConfig,
                borderConfig = defaultBorderConfig,
            )
        }
    )
}
