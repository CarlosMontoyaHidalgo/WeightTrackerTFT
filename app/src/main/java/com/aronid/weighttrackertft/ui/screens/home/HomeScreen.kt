package com.aronid.weighttrackertft.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.navigation.NavigationRoutes
import com.aronid.weighttrackertft.ui.components.BottomNavigationBar.BottomNavigationBar
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun HomeScreen(
    innerPadding: PaddingValues,
    navHostController: NavHostController,
    db: FirebaseFirestore
) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navHostController = navHostController) },
    ) { paddingValues ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(paddingValues)) {
            Text(
                text = stringResource(id = R.string.home),
                color = MaterialTheme.colorScheme.onBackground
            )
            Button(onClick = { navHostController.navigate("questionnaire") }) {
                Text(text = "Go to questionnaire")
            }

            Button(onClick = { navHostController.navigate(NavigationRoutes.Routines.route) }) {
                Text(text = "see routines")
            }

            Button(onClick = {navHostController.navigate(NavigationRoutes.CreateRoutine.route)}) {
                Text(text = "Create a routine")
            }
            Button(onClick = {navHostController.navigate(NavigationRoutes.Exercises.route)}) {
                Text(text = "Ver ejercicios")
            }
        }

    }
}
