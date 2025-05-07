package com.aronid.weighttrackertft.ui.components.searchBar.routines

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.navigation.NavigationRoutes
import com.aronid.weighttrackertft.ui.components.FloatingActionButton.MyFloatingActionButton
import com.aronid.weighttrackertft.ui.components.button.BackButton
import com.aronid.weighttrackertft.ui.components.routine.routineItem.RoutineItem
import com.aronid.weighttrackertft.ui.screens.routines.RoutineFilterType
import com.aronid.weighttrackertft.ui.screens.routines.RoutineViewModel


@Composable
fun RoutineSearchBar(
    viewModel: RoutineViewModel,
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {

    val state by viewModel.state.collectAsState()
    val buttonConfigs = state.baseState.buttonConfigs
    val searchText by viewModel.searchText.collectAsState()
    val customRoutines by viewModel.customRoutines.collectAsState()
    val predefinedRoutines by viewModel.predefinedRoutines.collectAsState()
    val favoriteRoutines by viewModel.favoriteRoutines.collectAsState()
    val filterType by viewModel.filterType.collectAsState()
    val context = LocalContext.current

    var showCheckbox by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showFilters by remember { mutableStateOf(false) }
    val selectedRoutines = viewModel.selectedRoutines

    val showFloatingActionButton =
        !(filterType == RoutineFilterType.CUSTOM_ONLY && customRoutines.isEmpty())

    LaunchedEffect(Unit) {
       viewModel.loadRoutines()
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                TextField(
                    value = searchText,
                    onValueChange = viewModel::onSearchTextChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp)),
                    placeholder = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_search),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.search_routines_placeholder))
                        }
                    },
                    trailingIcon = {
                        if (searchText.isNotEmpty()) {
                            IconButton(onClick = { viewModel.clearSearchQuery() }) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_x),
                                    contentDescription = stringResource(R.string.close)
                                )
                            }
                        }
                    },
                    singleLine = true
                )

                // Action Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            if (showCheckbox) {
                                viewModel.selectedRoutines.clear()
                            }
                            showCheckbox = !showCheckbox
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (showCheckbox) MaterialTheme.colorScheme.secondaryContainer
                            else MaterialTheme.colorScheme.primaryContainer
                        ),
                        enabled = customRoutines.isNotEmpty()
                    ) {
                        Text(if (showCheckbox) stringResource(R.string.cancel) else stringResource(R.string.edit))
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    if (selectedRoutines.isNotEmpty()) {
                        Button(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer
                            )
                        ) {
                            Text("Eliminar (${selectedRoutines.size})")
                        }
                    } else {
                        Button(
                            onClick = { showFilters = !showFilters },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (showFilters) MaterialTheme.colorScheme.secondaryContainer
                                else MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Text(stringResource(R.string.filter))
                        }
                    }
                }

                // Filters
                if (showFilters) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        FilterChip(
                            selected = filterType == RoutineFilterType.ALL,
                            onClick = { viewModel.setFilterType(RoutineFilterType.ALL) },
                            label = { Text(stringResource(R.string.all_routines)) }
                        )
                        FilterChip(
                            selected = filterType == RoutineFilterType.CUSTOM_ONLY,
                            onClick = { viewModel.setFilterType(RoutineFilterType.CUSTOM_ONLY) },
                            label = { Text(stringResource(R.string.my_routines)) }
                        )
                        FilterChip(
                            selected = filterType == RoutineFilterType.PREDEFINED_ONLY,
                            onClick = { viewModel.setFilterType(RoutineFilterType.PREDEFINED_ONLY) },
                            label = { Text(stringResource(R.string.predefined_routines_filter)) }
                        )
                        FilterChip(
                            selected = filterType == RoutineFilterType.FAVORITES,
                            onClick = { viewModel.setFilterType(RoutineFilterType.FAVORITES) },
                            label = { Text(stringResource(R.string.favorite_routines_filter)) }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (showFloatingActionButton) {
                MyFloatingActionButton(
                    onClick = {
                        navHostController.navigate(NavigationRoutes.CreateRoutine.route)
                    }
                )

            }
        },

        ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Delete Confirmation Dialog
            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Eliminar rutinas") },
                    text = { Text("¿Eliminar las ${selectedRoutines.size} rutinas seleccionadas?") },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.deleteSelectedRoutines { success ->
                                    if (success) {
                                        Toast.makeText(
                                            context,
                                            "Rutinas eliminadas",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        showCheckbox = false
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Error al eliminar",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    showDeleteDialog = false
                                }
                            }
                        ) {
                            Text("Confirmar")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text("Cancelar")
                        }
                    }
                )
            }

//            if (searchText.isEmpty() && customRoutines.isEmpty()) {
//                NewCustomButton(
//                    modifier = Modifier.padding(
//                        horizontal = 16.dp,
//                        vertical = 8.dp
//                    ),
//                    onClick = {
//
//                        navHostController.navigate(NavigationRoutes.CreateRoutine.route)
//                    },
//                    text = stringResource(R.string.create_new_routine),
//                    buttonType = ButtonType.ELEVATED,
//                    containerColor = Color.Blue,
//                    textConfig = buttonConfigs.textConfig.copy(textColor = Color.White),
//                    layoutConfig = buttonConfigs.layoutConfig,
//                    stateConfig = buttonConfigs.stateConfig,
//                    borderConfig = buttonConfigs.borderConfig,
//                    iconConfig = IconConfig(
//                        iconId = R.drawable.ic_add,
//                        iconPosition = IconPosition.END,
//                        iconContentDescription = stringResource(R.string.add_icon_description),
//                        iconSize = 24.dp,
//                        iconSpacing = 8.dp,
//                        iconTint = Color.White
//                    )
//                )
//            }
            // Content
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp)
            ) {


                if (filterType == RoutineFilterType.FAVORITES) {
                    item {
                        Text(
                            text = stringResource(R.string.favorite_routines),
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    if (favoriteRoutines.isEmpty()) {
                        item {
                            Text(
                                text = stringResource(R.string.no_favorite_routines),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    } else {
                        items(favoriteRoutines) { routine ->
                            RoutineItem(
                                routine = routine,
                                navHostController = navHostController,
                            )
                        }
                    }
                } else {
                    if (filterType != RoutineFilterType.FAVORITES) {
                        // Custom Routines Section
                        if (filterType != RoutineFilterType.PREDEFINED_ONLY) {
                            item {
                                Text(
                                    text = stringResource(R.string.my_routines),
                                    style = MaterialTheme.typography.headlineMedium,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                            //Empty Custom Routines
                            if (customRoutines.isEmpty()) {
                                item {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = if (searchText.isEmpty()) stringResource(R.string.no_custom_routines)
                                            else stringResource(R.string.no_custom_routines_found),
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )


                                    }
                                }
                            } else {
                                items(customRoutines) { routine ->
                                    RoutineItem(
                                        routine = routine,
                                        navHostController = navHostController,
                                        isPredefined = false,
                                        isChecked = selectedRoutines.containsKey(routine.id),
                                        showCheckbox = showCheckbox,
                                        onCheckedChange = { checked ->
                                            viewModel.toggleSelection(routine.id, checked)
                                        }
                                    )
                                }
                            }
                        }

                        // Predefined Routines Section
                        if (filterType != RoutineFilterType.CUSTOM_ONLY) {
                            item {
                                Text(
                                    text = stringResource(R.string.predefined_routines),
                                    style = MaterialTheme.typography.headlineMedium,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }

                            //Predefined Routines Empty
                            if (predefinedRoutines.isEmpty()) {
                                item {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        CircularProgressIndicator()
                                        Text(
                                            text = if (searchText.isEmpty()) stringResource(R.string.loading_predefined_routines)
                                            else stringResource(R.string.no_predefined_routines_found),
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(16.dp)
                                        )
                                    }
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
            }
        }
        BackButton(navHostController)

    }
}
