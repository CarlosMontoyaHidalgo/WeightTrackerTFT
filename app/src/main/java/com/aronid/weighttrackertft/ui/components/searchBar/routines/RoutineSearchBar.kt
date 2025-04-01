package com.aronid.weighttrackertft.ui.components.searchBar.routines

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.navigation.NavigationRoutes
import com.aronid.weighttrackertft.ui.components.button.NewCustomButton
import com.aronid.weighttrackertft.ui.components.routine.routineItem.RoutineItem
import com.aronid.weighttrackertft.ui.screens.routines.RoutineViewModel
import com.aronid.weighttrackertft.utils.button.ButtonType
import com.aronid.weighttrackertft.utils.button.IconConfig
import com.aronid.weighttrackertft.utils.button.IconPosition

@Composable
fun RoutineSearchBar(
    viewModel: RoutineViewModel,
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {
    val searchText by viewModel.searchText.collectAsState()
    val customRoutines by viewModel.customRoutines.collectAsState()
    val predefinedRoutines by viewModel.predefinedRoutines.collectAsState()
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val buttonConfigs = state.baseState.buttonConfigs

    val createNewRoutineMessage = stringResource(id = R.string.create_new_routine_toast)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        TextField(
            value = searchText,
            onValueChange = viewModel::onSearchTextChange,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp)),
            placeholder = {
                Row {
                    Image(
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.search_routines_placeholder))
                }
            },
            trailingIcon = {
                if (searchText.isNotEmpty()) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_x),
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            viewModel.clearSearchQuery()
                        }
                    )
                }
            },
            maxLines = 1,
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            item {
                Text(
                    text = stringResource(R.string.my_routines),
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                )
            }

            if (customRoutines.isEmpty() && searchText.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.no_custom_routines),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    NewCustomButton(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        onClick = {
                            Toast.makeText(context, createNewRoutineMessage, Toast.LENGTH_SHORT)
                                .show()
                            navHostController.navigate(NavigationRoutes.CreateRoutine.route)
                        },
                        text = stringResource(R.string.create_new_routine),
                        buttonType = ButtonType.ELEVATED,
                        containerColor = Color.Blue,
                        textConfig = buttonConfigs.textConfig.copy(textColor = Color.White),
                        layoutConfig = buttonConfigs.layoutConfig,
                        stateConfig = buttonConfigs.stateConfig,
                        borderConfig = buttonConfigs.borderConfig,
                        iconConfig = IconConfig(
                            iconId = R.drawable.ic_add,
                            iconPosition = IconPosition.END,
                            iconContentDescription = stringResource(R.string.add_icon_description),
                            iconSize = 24.dp,
                            iconSpacing = 8.dp,
                            iconTint = Color.White
                        )
                    )
                }
            } else if (customRoutines.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.no_custom_routines_found),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            } else {
                items(customRoutines) { routine ->
                    RoutineItem(
                        routine = routine,
                        navHostController = navHostController,
                        isPredefined = false
                    )
                }
            }

            item {
                Text(
                    text = stringResource(R.string.predefined_routines),
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                )
            }

            if (predefinedRoutines.isEmpty()) {
                item {
                    Text(
                        text = if (searchText.isEmpty()) stringResource(R.string.loading_predefined_routines) else stringResource(
                            R.string.no_predefined_routines_found
                        ),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            } else {
                items(predefinedRoutines) { routine ->
                    RoutineItem(
                        routine = routine,
                        navHostController = navHostController,
                        isPredefined = true
                    )
                }
            }
        }
    }
}