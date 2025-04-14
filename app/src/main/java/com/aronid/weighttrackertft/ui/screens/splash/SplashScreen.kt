package com.aronid.weighttrackertft.ui.screens.splash


import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.navigation.NavigationRoutes
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    innerPadding: PaddingValues, isUserLoggedIn: Boolean, navHostController: NavHostController
) {
    val scale = remember { Animatable(0.5f) }

    LaunchedEffect(Unit) {
        Log.d("SplashScreen", "LaunchedEffect called")
        scale.animateTo(
            targetValue = 1f, animationSpec = tween(durationMillis = 1000)
        )
        Log.d("SplashScreen", "Animation finished")
        delay(2000)/*Where it goes, check if its the user is logged in or not*/
        val destination =
            if (isUserLoggedIn) NavigationRoutes.Home.route else NavigationRoutes.Initial.route
        Log.d("SplashScreen", "Navigating to $destination")
        navHostController.navigate(destination) {
            popUpTo(NavigationRoutes.SplashScreen.route) { inclusive = true }
        }
    }
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Yellow)
            .padding(innerPadding),
        contentAlignment = Alignment.Center
    ) {
        Column {
            Image(
                painter = painterResource(id = R.drawable.app_logo_no_background), // Cambia "your_logo" por el nombre de tu archivo
                contentDescription = "Logo de la aplicación",
                modifier = Modifier.size((200 * scale.value).dp)
            )
            Text(
                text = "Weight Tracker TFT",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.size((200 * scale.value).dp)
            )

            CircularProgressIndicator()

        }
    }

}