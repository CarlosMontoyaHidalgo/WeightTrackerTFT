package com.aronid.weighttrackertft.ui.screens.questionnaire

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.navigation.NavigationRoutes
import com.aronid.weighttrackertft.ui.components.button.CustomButton
import com.aronid.weighttrackertft.ui.theme.Black
import com.aronid.weighttrackertft.ui.theme.White

@Composable
fun UserQuestionnaireScreen(
    innerPadding: PaddingValues,
    viewModel: UserQuestionnaireViewModel,
    navHostController: NavHostController,
) {
    var isChecked by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "📢 Importante sobre tu privacidad",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "En WeightTrackerTFT, nos tomamos muy en serio tu privacidad y seguridad. " +
                    "Los datos que ingreses en este cuestionario nos ayudarán a personalizar tu experiencia, " +
                    "brindándote recomendaciones y estadísticas adaptadas a tus objetivos.\n\n" +
                    "🔒 Seguridad garantizada: Toda la información se almacena de forma segura y solo será utilizada dentro de la aplicación.\n\n" +
                    "📊 Uso de datos: Los datos recopilados nos permitirán ofrecerte un mejor seguimiento de tu progreso, " +
                    "incluyendo análisis y gráficos personalizados.\n\n" +
                    "✅ Tú tienes el control: Puedes actualizar o eliminar tu información en cualquier momento desde la configuración de la app.\n\n" +
                    "Al continuar, aceptas que utilizaremos estos datos solo para mejorar tu experiencia en WeightTrackerTFT. 💪"
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isChecked,
                onCheckedChange = { isChecked = it }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "He leído y acepto el uso de mis datos")
        }

        Spacer(modifier = Modifier.height(16.dp))


        CustomButton(
            text = "Comenzar Cuestionario",
            containerColor = Black,
            textColor = White,
            enabled = isChecked,
            onClick = {
                navHostController.navigate(NavigationRoutes.PersonalInformation.route)
            }
        )

    }
}
