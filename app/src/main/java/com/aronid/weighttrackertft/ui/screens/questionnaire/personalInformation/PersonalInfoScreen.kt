package com.aronid.weighttrackertft.ui.screens.questionnaire.personalInformation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.ui.components.WheelDatePickerBottomSheet.WheelDatePickerBottomSheet
import com.aronid.weighttrackertft.ui.components.button.NewCustomButton
import com.aronid.weighttrackertft.ui.components.button.SmallButton
import com.aronid.weighttrackertft.ui.components.formScreen.FormScreen
import com.aronid.weighttrackertft.utils.button.ButtonType
import com.aronid.weighttrackertft.utils.formatDate

@Composable
fun PersonalInformationScreen(
    innerPadding: PaddingValues,
    viewModel: PersonalInfoViewModel,
    navHostController: NavHostController
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val state by viewModel.state.collectAsState()
    val buttonConfigs = state.buttonConfigs


    FormScreen(
        modifier = Modifier,
        innerPadding = innerPadding,
        isContentScrolleable = false,
        formContent = {
            Text(stringResource(id = R.string.personal_info), color = Color.Black)

            OutlinedTextField(
                value = state.name,
                onValueChange = viewModel::onNameChanged,
                label = { Text(stringResource(id = R.string.name)) },
                modifier = Modifier.fillMaxWidth(),
                isError = state.nameTouched && !state.isNameValid,
                supportingText = {
                    if (state.nameTouched && !state.isNameValid) {
                        Text("Este campo es obligatorio", color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = formatDate(state.birthdate),
                    onValueChange = { newDate ->
                        viewModel.onBirthdateChanged(newDate)
                    },
                    label = { Text(stringResource(id = R.string.birth_date)) },
                    modifier = Modifier.weight(2f),
                    singleLine = true,
                    isError = state.birthdateTouched && !state.isBirthdateValid,
                    supportingText = {
                        if (state.birthdateTouched && !state.isBirthdateValid) {
                            Text(
                                "Formato de fecha inválido (DD-MM-YYYY)",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )
                Spacer(Modifier.width(16.dp))
                SmallButton(
                    imageId = R.drawable.ic_calendar,
                    onClick = { showDatePicker = true },
                    modifier = Modifier.weight(1f)
                )
            }

            if (showDatePicker) {
                WheelDatePickerBottomSheet(
                    onDateSelected = { date ->
                        viewModel.onBirthdateChanged(date)
                        showDatePicker = false
                    },
                    onDismiss = { showDatePicker = false }
                )
            }
        },
        formButton = {


//            CustomButton(
//                text = stringResource(id = R.string.next),
//                textColor = MaterialTheme.colorScheme.onPrimary,
//                containerColor = MaterialTheme.colorScheme.onBackground,
//                onClick = {
//                    viewModel.uploadData(navHostController)
//                }
//            )

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

