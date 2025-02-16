package com.aronid.weighttrackertft.data.questionnaire

import androidx.annotation.StringRes
import com.aronid.weighttrackertft.utils.button.BorderConfig
import com.aronid.weighttrackertft.utils.button.LayoutConfig
import com.aronid.weighttrackertft.utils.button.StateConfig
import com.aronid.weighttrackertft.utils.button.TextConfig

data class UserQuestionnaireState(
    val isFormValid: Boolean = false,
    val buttonConfigs: ButtonConfigs = ButtonConfigs(
        textConfig = TextConfig(),
        layoutConfig = LayoutConfig(),
        stateConfig = StateConfig(),
        borderConfig = BorderConfig()
    )
)

data class PersonalInfoState(
    val name: String = "",
    val isNameValid: Boolean = true,
    val nameTouched: Boolean = false,
    val birthdate: String = "",
    val isBirthdateValid: Boolean = true,
    val birthdateTouched: Boolean = false,
    val gender: String = "",
    val isFormValid: Boolean = false,
    val buttonConfigs: ButtonConfigs = ButtonConfigs(
        textConfig = TextConfig(),
        layoutConfig = LayoutConfig(),
        stateConfig = StateConfig(),
        borderConfig = BorderConfig()
    )
)

data class PhysicalDataState(
    val height: Int? = null,
    val weight: Double? = null,
    val heightError: Int? = null,
    val weightError: Int? = null,
    val isHeightValid: Boolean = true,
    val isWeightValid: Boolean = true
)

data class LifeStyleState(
    val activityLevel: String = "",
    val isFormValid: Boolean = false,
    val buttonConfigs: ButtonConfigs = ButtonConfigs(
        textConfig = TextConfig(),
        layoutConfig = LayoutConfig(),
        stateConfig = StateConfig(),
        borderConfig = BorderConfig()
    )
)

data class GoalState(
    val goal: String = "",
    val isFormValid: Boolean = false,
    val buttonConfigs: ButtonConfigs = ButtonConfigs(
        textConfig = TextConfig(),
        layoutConfig = LayoutConfig(),
        stateConfig = StateConfig(),
        borderConfig = BorderConfig()
    )
)

data class ButtonConfigs(
    val textConfig: TextConfig,
    val layoutConfig: LayoutConfig,
    val stateConfig: StateConfig,
    val borderConfig: BorderConfig
)

data class LifeStyleOption(
    val id: String,
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int
)