package com.aronid.weighttrackertft.ui.components.button.ButtonList

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.aronid.weighttrackertft.R

@Composable
fun ButtonList(navHostController: NavHostController) {
    val buttonItems = listOf(
        "User Settings" to "user_settings",
        "Customization" to "customization",
        "Goals" to "goals",
        "History" to "history"
    )

    val iconMap = mapOf(
        "User Settings" to R.drawable.ic_user,
        "Customization" to R.drawable.ic_custom,
        "Goals" to R.drawable.ic_goals,
        "Equipment" to R.drawable.ic_dumbell
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        buttonItems.forEach { (label, route) ->
            ButtonItem(
                label = label,
                route = route,
                iconId = iconMap[label] ?: R.drawable.ic_user,
                navHostController = navHostController
            )
        }
    }
}

@Composable
fun ButtonItem(label: String, route: String, iconId: Int, navHostController: NavHostController) {
    Button(
        onClick = { navHostController.navigate(route) },
        modifier = Modifier
            .fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = iconId),
                    contentDescription = "$label Icon"

                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = label)
            }
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_right),
                contentDescription = "Arrow Forward"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ButtonListPreview() {
    val navController = rememberNavController()
    ButtonList(navHostController = navController)
}