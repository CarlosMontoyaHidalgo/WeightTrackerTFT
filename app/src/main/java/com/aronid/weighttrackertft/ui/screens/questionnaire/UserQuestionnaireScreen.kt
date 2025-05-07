package com.aronid.weighttrackertft.ui.screens.questionnaire

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.ui.components.button.NewCustomButton
import com.aronid.weighttrackertft.ui.components.formScreen.FormScreen
import com.aronid.weighttrackertft.utils.button.ButtonType

@Composable
fun UserQuestionnaireScreen(
    innerPadding: PaddingValues,
    viewModel: UserQuestionnaireViewModel,
    navHostController: NavHostController,
) {
    val state by viewModel.state.collectAsState()
    val buttonConfigs = state.baseState.buttonConfigs

    FormScreen(
        modifier = Modifier,
        innerPadding = innerPadding,
        isContentScrolleable = true,
        formContent = {
            Text(
                text = stringResource(id = R.string.privacy_policy),
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = R.string.privacy_policy_description),
            )


        },
        formButton = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = state.baseState.isFormValid,
                    onCheckedChange = { viewModel.onCheckboxChanged(it) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(id = R.string.accept_terms_and_conditions))
            }

            NewCustomButton(
                text = stringResource(id = R.string.start_questionnaire),
                onClick = {
                    viewModel.uploadData(navHostController)
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
