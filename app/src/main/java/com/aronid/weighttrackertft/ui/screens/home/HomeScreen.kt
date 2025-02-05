package com.aronid.weighttrackertft.ui.screens.home

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.navigation.NavigationRoutes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun HomeScreen(
    innerPadding: PaddingValues,
    navHostController: NavHostController,
    db: FirebaseFirestore
) {

    Column(modifier = Modifier.padding(innerPadding)) {
        Text(text = "Bienvenido a Home")
        Button(onClick = {
            FirebaseAuth.getInstance().signOut()
            navHostController.navigate(NavigationRoutes.Initial.route) {
                popUpTo(NavigationRoutes.Home.route) { inclusive = true }
            }
        }) {
            Text(text = "Cerrar Sesión")
        }
        Button(onClick = {createUser(db)}) {
            Text(text = "Crear Usuario")
        }
    }


}

data class User(
    val name: String,
    val email: String,

    )

fun createUser(db: FirebaseFirestore) {
    val random = (1..10000).random()
    val users = User("User $random", "$random@gmail.com")
    db.collection("users").add(users)
        .addOnSuccessListener {
            Log.i("User", "SUCCESS")
        }
        .addOnFailureListener {
            Log.i("User", "FAILURE")
        }
        .addOnCompleteListener {
            Log.i("User", "COMPLETE")
        }
}