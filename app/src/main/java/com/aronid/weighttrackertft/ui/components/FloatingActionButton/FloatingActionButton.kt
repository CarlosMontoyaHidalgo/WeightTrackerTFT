package com.aronid.weighttrackertft.ui.components.FloatingActionButton

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aronid.weighttrackertft.R

@Preview(showBackground = true)
@Composable
fun FloatingActionButtonPreview() {
    Column (modifier = Modifier.padding(50.dp)){

        MyFloatingActionButton(
            onClick = {}
        )
        Spacer(modifier = Modifier.height(16.dp))
        MyFloatingActionButton(
            onClick = {},
            shape = CircleShape,
            contentColor = Color.White,
            containerColor = Color.Blue,
            icon = R.drawable.ic_add
        )
        Spacer(modifier = Modifier.height(16.dp))
        MyFloatingActionButton(
            onClick = {},
            shape = RoundedCornerShape(8.dp),
            contentColor = Color.White,
            containerColor = Color.Blue,
            icon = R.drawable.ic_add
        )
        Spacer(modifier = Modifier.height(16.dp))
        MyFloatingActionButton(
            onClick = {},
            shape = RoundedCornerShape(8.dp)
        )

    }
}

/**
 * Componente de botón de acción flotante (FAB) mejorado.
 *
 * @param onClick Acción a ejecutar al hacer clic en el botón.
 * @param modifier Modifier para aplicar estilos o posicionamiento externo.
 * @param shape Forma del botón (por defecto CircleShape).
 * @param contentColor Color del contenido (icono) dentro del botón.
 * @param containerColor Color de fondo del botón.
 * @param icon Recurso del icono a mostrar.
 * @param contentDescription Descripción del contenido, útil para accesibilidad.
 */
@Composable
fun MyFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    contentColor: Color = Color.White,
    containerColor: Color = Color.Blue,
    icon: Int = R.drawable.ic_add,
    contentDescription: String = "Add"
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        shape = shape,
        contentColor = contentColor,
        containerColor = containerColor,
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = contentDescription
        )
    }
}

