package com.aronid.weighttrackertft.ui.screens.user.personalInformation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.data.goals.getGoalOptions
import com.aronid.weighttrackertft.ui.components.WheelDatePickerBottomSheet.WheelDatePickerBottomSheet
import com.aronid.weighttrackertft.ui.components.button.BackButton
import com.aronid.weighttrackertft.ui.components.button.NewCustomButton
import com.aronid.weighttrackertft.ui.components.dropdown.weightUnitSelector.WeightUnitSelectorViewModel
import com.aronid.weighttrackertft.ui.components.fields.defaultField.DefaultField
import com.aronid.weighttrackertft.ui.components.formScreen.FormScreen
import com.aronid.weighttrackertft.utils.button.ButtonType
import com.aronid.weighttrackertft.utils.formatDate

@Composable
fun UserDataScreen(
    innerPadding: PaddingValues,
    viewModel: UserDataViewModel,
    weightUnitViewModel: WeightUnitSelectorViewModel, // Podríamos eliminar esto si usamos WeightRepository directamente
    navHostController: NavHostController
) {
    val state by viewModel.state.collectAsState()
    val buttonState by viewModel.buttonState.collectAsState()
    val buttonConfigs = buttonState.baseState.buttonConfigs
    var showDatePicker by remember { mutableStateOf(false) }
    val weightUnit by viewModel.weightUnit.collectAsState() // Usamos el weightUnit del ViewModel

    var expandedGoal by remember { mutableStateOf(false) }
    val goalOptions = getGoalOptions()
    val density = LocalDensity.current
    var dropdownOffset by remember { mutableStateOf(DpOffset.Zero) }
    var anchorHeight by remember { mutableStateOf(0.dp) }

    val showNameError by viewModel.showNameError.collectAsState()
    val showBirthdateError by viewModel.showBirthdateError.collectAsState()
    val showEmailError by viewModel.showEmailError.collectAsState()
    val showHeightError by viewModel.showHeightError.collectAsState()
    val showWeightError by viewModel.showWeightError.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadUserData()
    }

    FormScreen(
        modifier = Modifier,
        innerPadding = innerPadding,
        isContentScrolleable = true,
        formContent = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxHeight()
            ) {
                Text(stringResource(id = R.string.personal_data))

                Spacer(modifier = Modifier.height(16.dp))

                DefaultField(
                    text = state.name,
                    onTextChange = { viewModel.onNameChanged(it) },
                    label = stringResource(id = R.string.name),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    iconId = R.drawable.ic_user,
                    validate = viewModel::validateName,
                    showError = showNameError
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    DefaultField(
                        text = formatDate(state.birthdate),
                        onTextChange = viewModel::onBirthdateChanged,
                        label = stringResource(id = R.string.birth_date),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        iconId = R.drawable.ic_calendar,
                        validate = viewModel::validateBirthdate,
                        showError = showBirthdateError,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePicker = true }
                    )

                    Button(
                        onClick = { showDatePicker = true },
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 8.dp)
                            .background(Color.Transparent)
                            .fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            disabledContentColor = Color.Transparent
                        )
                    ) {}
                }

                DefaultField(
                    text = state.email,
                    onTextChange = { viewModel.onEmailChanged(it) },
                    label = stringResource(id = R.string.email),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    iconId = R.drawable.ic_email,
                    validate = viewModel::validateEmail,
                    showError = showEmailError
                )

                DefaultField(
                    text = state.height?.toString() ?: "",
                    onTextChange = { viewModel.onHeightChanged(it) },
                    label = stringResource(id = R.string.height_cm),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    iconId = R.drawable.ic_height,
                    validate = viewModel::validateHeight,
                    showError = showHeightError
                )

                DefaultField(
                    text = state.weight?.let {
                        if (weightUnit == "Libras") (it / 0.453592).toString() else it.toString()
                    } ?: "",
                    onTextChange = { viewModel.onWeightChanged(it) },
                    label = stringResource(id = R.string.weight_label, weightUnit.lowercase()),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    iconId = R.drawable.ic_weight,
                    validate = viewModel::validateWeight,
                    showError = showWeightError
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color.Black, RoundedCornerShape(4.dp))
                            .height(56.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .clickable { expandedGoal = true }
                            .padding(12.dp)
                            .onSizeChanged { size ->
                                with(density) {
                                    anchorHeight = size.height.toDp()
                                    dropdownOffset = DpOffset(
                                        x = 0.dp,
                                        y = anchorHeight + 0.dp
                                    )
                                }
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = viewModel.getGoalDisplay(state.goal),
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_down),
                            contentDescription = stringResource(id = R.string.expand_goal_options),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    DropdownMenu(
                        expanded = expandedGoal,
                        onDismissRequest = { expandedGoal = false },
                        offset = dropdownOffset,
                        modifier = Modifier
                            .fillMaxWidth(0.95f)
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(8.dp)
                            )
                    ) {
                        goalOptions.forEach { option ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = option.title,
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                },
                                onClick = {
                                    viewModel.onGoalChanged(option.id)
                                    expandedGoal = false
                                },
                                modifier = Modifier
                                    .background(
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .fillMaxWidth()
                            )
                        }
                    }
                }
            }
            BackButton(navHostController)
        },
        formButton = {
            NewCustomButton(
                text = stringResource(id = R.string.save),
                onClick = { viewModel.saveUserData() },
                buttonType = ButtonType.FILLED,
                containerColor = Color.Black,
                textConfig = buttonConfigs.textConfig,
                layoutConfig = buttonConfigs.layoutConfig,
                stateConfig = buttonConfigs.stateConfig,
                borderConfig = buttonConfigs.borderConfig
            )
        }
    )

    if (showDatePicker) {
        WheelDatePickerBottomSheet(
            onDateSelected = { date ->
                viewModel.onBirthdateChanged(date)
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}