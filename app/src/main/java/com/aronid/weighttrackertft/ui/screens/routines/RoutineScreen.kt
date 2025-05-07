package com.aronid.weighttrackertft.ui.screens.routines

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.ui.components.searchBar.routines.RoutineSearchBar

@Composable
fun RoutineScreen(
    innerPadding: PaddingValues,
    viewModel: RoutineViewModel,
    navHostController: NavHostController
) {
    Column(modifier = Modifier.padding(innerPadding)) {
        RoutineSearchBar(
            viewModel = viewModel,
            navHostController = navHostController,
            modifier = Modifier.weight(1f)
        )
    }
}



