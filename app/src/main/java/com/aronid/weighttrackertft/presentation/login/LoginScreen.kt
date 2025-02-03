package com.aronid.weighttrackertft.presentation.login

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.aronid.weighttrackertft.R
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.ui.components.authForm.AuthForm
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(
    innerPadding: PaddingValues,
    auth: FirebaseAuth,
    navHostController: NavHostController
) {

    Column(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .background(color = Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_back),
            contentDescription = "Back",
            tint = Color.Black,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(vertical = 16.dp)
                .clickable { navHostController.popBackStack() }
        )
        Text(text = stringResource(id = R.string.login), modifier = Modifier.padding(bottom = 16.dp), fontSize = 24.sp)
        AuthForm(stringResource(id = R.string.login)){
            email, password ->
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener{
                task ->
                if (task.isSuccessful){
                    //navegar al home
                    Log.i("LoginScreen", "Usuario logueado")
                } else {
                    //mostrar error
                    Log.e("LoginScreen", "Error al loguear usuario", task.exception)
                }
            }
        }

    }
}
