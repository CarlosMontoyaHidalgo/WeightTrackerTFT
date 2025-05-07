package com.aronid.weighttrackertft.ui.components.navigationBar.BottomNavigationBar

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
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
        BottomNavItem(
            NavigationRoutes.Home.route,
            R.drawable.ic_home,
            stringResource(R.string.home)
        ),
        BottomNavItem(
            NavigationRoutes.Stats.route,
            R.drawable.ic_stats,
            stringResource(R.string.progress)
        ),
        BottomNavItem(
            NavigationRoutes.Exercises.route,
            R.drawable.ic_dumbell,
            stringResource(R.string.exercises)
        ),
        BottomNavItem(
            NavigationRoutes.Settings.route,
            R.drawable.ic_settings,
            stringResource(R.string.settings)
        )
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        modifier = Modifier
            .clip(RoundedCornerShape(topStart = 8.dp))
            .border(2.dp, Color.LightGray, RoundedCornerShape(topStart = 8.dp))
    ) {
        items.forEach { item ->
            val isSelected = navHostController.currentDestination?.route == item.route

            NavigationBarItem(
                selected = isSelected, onClick = {
                if (!isSelected) {
                    navHostController.navigate(item.route) {
                        popUpTo(NavigationRoutes.Home.route) { inclusive = true }
                        launchSingleTop = true
                    }

                }
            }, icon = {
                AnimatedContent(
                    targetState = isSelected,
                    transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) },
                    label = "IconTransition"
                ) { selected ->
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = "${item.label} navigation item",
                        tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(if (isSelected) 28.dp else 24.dp)
                    )
                }
            }, label = {
                Text(
                    text = item.label, color = if (isSelected) Color.Blue else Color.Black
                )
            },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.semantics { contentDescription = "Navigate to ${item.label}" }

            )
        }
    }
}

/*
* Opcional:
* -BadgeBox
*
*
* */