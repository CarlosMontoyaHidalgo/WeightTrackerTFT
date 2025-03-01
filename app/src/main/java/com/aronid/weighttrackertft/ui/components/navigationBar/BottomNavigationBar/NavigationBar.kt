package com.aronid.weighttrackertft.ui.components.navigationBar.BottomNavigationBar

import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.navigation.NavigationRoutes

@Preview(showBackground = true)
@Composable
fun BottomNavigationBarPreview() {
    BottomNavigationBar(navHostController = NavHostController(LocalContext.current))
}

@Composable
fun BottomNavigationBar(navHostController: NavHostController) {
    val items = listOf(
        BottomNavItem(NavigationRoutes.Home.route, R.drawable.ic_home, "Inicio"),
        BottomNavItem(NavigationRoutes.Stats.route, R.drawable.ic_stats, "Progreso"),
        BottomNavItem(NavigationRoutes.Exercises.route, R.drawable.ic_dumbell, "Ejercicios"),
        BottomNavItem(NavigationRoutes.Settings.route, R.drawable.ic_settings, "Ajustes")
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .clip(RoundedCornerShape(topStart = 8.dp))
            .border(2.dp, Color.LightGray, RoundedCornerShape(topStart = 8.dp))
    ) {
        items.forEach { item ->
            val isSelected = navHostController.currentDestination?.route == item.route

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navHostController.navigate(item.route) {
                        popUpTo(NavigationRoutes.Home.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = item.label,
                        tint = if (isSelected) Color.Blue else Color.Black
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        color = if (isSelected) Color.Blue else Color.Black
                    )
                }
            )
        }
    }
}