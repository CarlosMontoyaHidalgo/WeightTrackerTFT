package com.aronid.weighttrackertft.ui.screens.routines

import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.ui.components.button.NewCustomButton
import com.aronid.weighttrackertft.ui.components.loading.LoadingState
import com.aronid.weighttrackertft.ui.components.routine.routineItem.RoutineItem
import com.aronid.weighttrackertft.utils.button.ButtonType
import com.aronid.weighttrackertft.utils.button.IconConfig
import com.aronid.weighttrackertft.utils.button.IconPosition

@Composable
fun RoutineScreen(
    innerPadding: PaddingValues,
    viewModel: RoutineViewModel,
    navHostController: NavHostController
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val customRoutines by viewModel.customRoutines.collectAsState()
    val predefinedRoutines by viewModel.predefinedRoutines.collectAsState()
    val buttonConfigs = state.baseState.buttonConfigs

    LazyColumn(
        modifier = Modifier
            .padding(innerPadding)
    ) {
        item {
            Text(
                text = "Mis Rutinas",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp)
            )
        }

        if (customRoutines.isEmpty()) {
            item {
                NewCustomButton(
                    modifier = Modifier,
                    onClick = {
                        Toast.makeText(context, "Crear nueva rutina", Toast.LENGTH_SHORT).show()
                    },
                    text = "Crear nueva rutina",
                    buttonType = ButtonType.ELEVATED,
                    containerColor = Color.Blue,
                    textConfig = buttonConfigs.textConfig.copy(textColor = Color.White),
                    layoutConfig = buttonConfigs.layoutConfig,
                    stateConfig = buttonConfigs.stateConfig,
                    borderConfig = buttonConfigs.borderConfig,
                    iconConfig = IconConfig(
                        iconId = R.drawable.ic_add,
                        iconPosition = IconPosition.END,
                        iconContentDescription = "Icon End",
                        iconSize = 24.dp,
                        iconSpacing = 8.dp,
                        iconTint = Color.White
                    )
                )
            }
        } else {
            items(customRoutines) { routine ->
                RoutineItem(routine, navHostController)
            }
        }

        item {
            Text(
                text = "Rutinas Predefinidas",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp)
            )
        }

        if (predefinedRoutines.isEmpty()) {
            item { LoadingState() }
        } else {
            items(predefinedRoutines) { routine ->
                RoutineItem(routine, navHostController)
            }
        }
    }
}



