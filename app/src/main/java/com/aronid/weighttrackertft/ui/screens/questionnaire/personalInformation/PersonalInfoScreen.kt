package com.aronid.weighttrackertft.ui.screens.questionnaire.personalInformation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.ui.screens.questionnaire.UserQuestionnaireViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.ui.components.button.CustomButton
import com.aronid.weighttrackertft.ui.components.button.SmallButton
import com.aronid.weighttrackertft.ui.theme.Black
import com.aronid.weighttrackertft.ui.theme.White
import network.chaintech.kmp_date_time_picker.ui.datepicker.WheelDatePickerView
import network.chaintech.kmp_date_time_picker.utils.DateTimePickerView

@Composable
fun PersonalInformationScreen(
    innerPadding: PaddingValues,
    viewModel: UserQuestionnaireViewModel,
    navController: NavHostController
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(innerPadding)) {
        Text(stringResource(id = R.string.personal_info))

        TextField(
            value = viewModel.state.collectAsState().value.personalInfo.name,
            onValueChange = { newName ->
                viewModel.updatePersonalInfo(
                    name = newName,
                    birthdate = viewModel.state.value.personalInfo.birthdate,
                    gender = viewModel.state.value.personalInfo.gender
                )
            },
            label = { Text(stringResource(id = R.string.name)) },
            modifier = Modifier.fillMaxWidth()
        )

        Row() {
            OutlinedTextField(
                value = viewModel.state.collectAsState().value.personalInfo.birthdate,
                onValueChange = { newBirthdate ->
                    viewModel.updatePersonalInfo(
                        name = viewModel.state.value.personalInfo.name,
                        birthdate = newBirthdate,
                        gender = viewModel.state.value.personalInfo.gender
                    )
                },
                label = { Text(stringResource(id = R.string.birth_date)) },
                modifier = Modifier.fillMaxWidth()
            )
            SmallButton(
                imageId = R.drawable.ic_calendar,
                onClick = {
                    showDatePicker = true
                }
            )
        }

        CustomButton(
            text = stringResource(id = R.string.next),
            textColor = MaterialTheme.colorScheme.onPrimary,
            containerColor = MaterialTheme.colorScheme.onBackground,
            onClick = {
                navController.navigate("lifeStyle")
            })
    }
}

@Composable
fun DatePicker(viewModel: UserQuestionnaireViewModel) {
    WheelDatePickerView(
        height = 200.dp,
        onDoneClick = {
            viewModel.updatePersonalInfo(
                name = viewModel.state.value.personalInfo.name,
                birthdate = it.toString(),
                gender = viewModel.state.value.personalInfo.gender
            )
        },
        dateTimePickerView = DateTimePickerView.DIALOG_VIEW,
        rowCount = 3,
        titleStyle = TextStyle(
            fontSize = 20.sp,
            color = White,
            background = Black
        ),
        doneLabelStyle = TextStyle(
            fontSize = 20.sp,
            color = White,
            background = Black
        ),

        )
}